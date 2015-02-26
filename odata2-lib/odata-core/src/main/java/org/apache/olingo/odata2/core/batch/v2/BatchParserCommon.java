/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 ******************************************************************************/
package org.apache.olingo.odata2.core.batch.v2;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.olingo.odata2.api.batch.BatchException;
import org.apache.olingo.odata2.api.commons.HttpContentType;
import org.apache.olingo.odata2.api.commons.HttpHeaders;
import org.apache.olingo.odata2.core.batch.AcceptParser;
import org.apache.olingo.odata2.core.batch.v2.BufferedReaderIncludingLineEndings.Line;
import org.apache.olingo.odata2.core.commons.Decoder;

public class BatchParserCommon {

  private static final Pattern PATTERN_LAST_CRLF = Pattern.compile("(.*)(\r\n){1}( *)", Pattern.DOTALL);

  private static final String REG_EX_BOUNDARY =
      "([a-zA-Z0-9_\\-\\.'\\+]{1,70})|\"([a-zA-Z0-9_\\-\\.'\\+\\s\\" +
          "(\\),/:=\\?]{1,69}[a-zA-Z0-9_\\-\\.'\\+\\(\\),/:=\\?])\""; // See RFC 2046

  public static final Pattern PATTERN_MULTIPART_MIXED = Pattern
      .compile("multipart/mixed(.*)", Pattern.CASE_INSENSITIVE);
  final static String REG_EX_APPLICATION_HTTP = "application/http";
  public static final Pattern PATTERN_HEADER_LINE = Pattern.compile("([a-zA-Z\\-]+):\\s?(.*)\\s*");
  public static final Pattern PATTERN_CONTENT_TYPE_APPLICATION_HTTP = Pattern.compile(REG_EX_APPLICATION_HTTP,
      Pattern.CASE_INSENSITIVE);
  public static final Pattern PATTERN_RELATIVE_URI = Pattern.compile("([^/][^?]*)(\\?.*)?");

  public static String trimLineListToLength(final List<Line> list, final int length) {
    final String message = lineListToString(list);
    final int lastIndex = Math.min(length, message.length());

    return (lastIndex > 0) ? message.substring(0, lastIndex) : "";
  }

  public static String lineListToString(final List<Line> list) {
    StringBuilder builder = new StringBuilder();

    for (Line currentLine : list) {
      builder.append(currentLine.toString());
    }

    return builder.toString();
  }

  public static InputStream convertLineListToInputStream(final List<Line> messageList, final int contentLength)
      throws BatchException {
    final String message = trimLineListToLength(messageList, contentLength);

    return new ByteArrayInputStream(message.getBytes());
  }

  public static InputStream convertLineListToInputStream(final List<Line> messageList)
      throws BatchException {
    final String message = lineListToString(messageList);

    return new ByteArrayInputStream(message.getBytes());
  }

  static List<List<Line>> splitMessageByBoundary(final List<Line> message, final String boundary)
      throws BatchException {
    final List<List<Line>> messageParts = new LinkedList<List<Line>>();
    List<Line> currentPart = new ArrayList<Line>();
    boolean isEndReached = false;

    final String quotedBoundary = Pattern.quote(boundary);
    final Pattern boundaryDelimiterPattern = Pattern.compile("--" + quotedBoundary + "--[\\s ]*");
    final Pattern boundaryPattern = Pattern.compile("--" + quotedBoundary + "[\\s ]*");

    for (Line currentLine : message) {
      if (boundaryDelimiterPattern.matcher(currentLine.toString()).matches()) {
        removeEndingCRLFFromList(currentPart);
        messageParts.add(currentPart);
        isEndReached = true;
      } else if (boundaryPattern.matcher(currentLine.toString()).matches()) {
        removeEndingCRLFFromList(currentPart);
        messageParts.add(currentPart);
        currentPart = new LinkedList<Line>();
      } else {
        currentPart.add(currentLine);
      }

      if (isEndReached) {
        break;
      }
    }

    final int lineNumer = (message.size() > 0) ? message.get(0).getLineNumber() : 0;
    // Remove preamble
    if (messageParts.size() > 0) {
      messageParts.remove(0);
    } else {

      throw new BatchException(BatchException.MISSING_BOUNDARY_DELIMITER.addContent(lineNumer));
    }

    if (!isEndReached) {
      throw new BatchException(BatchException.MISSING_CLOSE_DELIMITER.addContent(lineNumer));
    }

    if (messageParts.size() == 0) {
      throw new BatchException(BatchException.NO_MATCH_WITH_BOUNDARY_STRING.addContent(boundary).addContent(lineNumer));
    }

    return messageParts;
  }

  private static void removeEndingCRLFFromList(final List<Line> list) {
    if (list.size() > 0) {
      Line lastLine = list.remove(list.size() - 1);
      list.add(removeEndingCRLF(lastLine));
    }
  }

  public static Line removeEndingCRLF(final Line line) {
    Pattern pattern = PATTERN_LAST_CRLF;
    Matcher matcher = pattern.matcher(line.toString());

    if (matcher.matches()) {
      return new Line(matcher.group(1), line.getLineNumber());
    } else {
      return line;
    }
  }

  public static Header consumeHeaders(final List<Line> remainingMessage) throws BatchException {
    final int headerLineNumber = remainingMessage.size() != 0 ? remainingMessage.get(0).getLineNumber() : 0;
    final Header headers = new Header(headerLineNumber);
    final Iterator<Line> iter = remainingMessage.iterator();
    final AcceptParser acceptParser = new AcceptParser();
    Line currentLine;
    int acceptLineNumber = 0;
    int acceptLanguageLineNumber = 0;
    boolean isHeader = true;

    while (iter.hasNext() && isHeader) {
      currentLine = iter.next();
      final Matcher headerMatcher = PATTERN_HEADER_LINE.matcher(currentLine.toString());

      if (headerMatcher.matches() && headerMatcher.groupCount() == 2) {
        iter.remove();

        String headerName = headerMatcher.group(1).trim();
        String headerValue = headerMatcher.group(2).trim();

        if (HttpHeaders.ACCEPT.equalsIgnoreCase(headerName)) {
          acceptParser.addAcceptHeaderValue(headerValue);
          acceptLineNumber = currentLine.getLineNumber();
        } else if (HttpHeaders.ACCEPT_LANGUAGE.equalsIgnoreCase(headerName)) {
          acceptParser.addAcceptLanguageHeaderValue(headerValue);
          acceptLanguageLineNumber = currentLine.getLineNumber();
        } else {
          headers.addHeader(headerName, Header.splitValuesByComma(headerValue), currentLine.getLineNumber());
        }
      } else {
        isHeader = false;
      }
    }

    headers.addHeader(HttpHeaders.ACCEPT, acceptParser.parseAcceptHeaders(), acceptLineNumber);
    headers.addHeader(HttpHeaders.ACCEPT_LANGUAGE, acceptParser.parseAcceptableLanguages(), acceptLanguageLineNumber);

    return headers;
  }

  public static void consumeBlankLine(final List<Line> remainingMessage, final boolean isStrict)
      throws BatchException {
    if (remainingMessage.size() > 0 && remainingMessage.get(0).toString().matches("\\s*\r\n\\s*")) {
      remainingMessage.remove(0);
    } else {
      if (isStrict) {
        final int lineNumber = (remainingMessage.size() > 0) ? remainingMessage.get(0).getLineNumber() : 0;
        throw new BatchException(BatchException.MISSING_BLANK_LINE.addContent("[None]").addContent(lineNumber));
      }
    }
  }

  public static String getBoundary(final String contentType, final int line) throws BatchException {
    if (contentType.toLowerCase(Locale.ENGLISH).startsWith("multipart/mixed")) {
      final String[] parameter = contentType.split(";");

      for (final String pair : parameter) {

        final String[] attrValue = pair.split("=");
        if (attrValue.length == 2 && "boundary".equals(attrValue[0].trim().toLowerCase(Locale.ENGLISH))) {
          if (attrValue[1].matches(REG_EX_BOUNDARY)) {
            return trimQuota(attrValue[1].trim());
          } else {
            throw new BatchException(BatchException.INVALID_BOUNDARY.addContent(line));
          }
        }

      }
    }
    throw new BatchException(BatchException.INVALID_CONTENT_TYPE.addContent(HttpContentType.MULTIPART_MIXED));
  }

  private static String trimQuota(String boundary) {
    if (boundary.matches("\".*\"")) {
      boundary = boundary.replace("\"", "");
    }

    return boundary;
  }

  public static Map<String, List<String>> parseQueryParameter(final Line httpRequest) {
    Map<String, List<String>> queryParameter = new HashMap<String, List<String>>();

    String[] requestParts = httpRequest.toString().split(" ");
    if (requestParts.length == 3) {

      String[] parts = requestParts[1].split("\\?");
      if (parts.length == 2) {
        String[] parameters = parts[1].split("&");

        for (String parameter : parameters) {
          String[] parameterParts = parameter.split("=");
          String parameterName = parameterParts[0].toLowerCase(Locale.ENGLISH);

          if (parameterParts.length == 2) {
            List<String> valueList = queryParameter.get(parameterName);
            valueList = valueList == null ? new LinkedList<String>() : valueList;
            queryParameter.put(parameterName, valueList);

            String[] valueParts = parameterParts[1].split(",");
            for (String value : valueParts) {
              valueList.add(Decoder.decode(value));
            }
          }
        }
      }
    }

    return queryParameter;
  }
}
