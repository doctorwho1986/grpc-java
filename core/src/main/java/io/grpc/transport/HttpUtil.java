/*
 * Copyright 2014, Google Inc. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *    * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *    * Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following disclaimer
 * in the documentation and/or other materials provided with the
 * distribution.
 *
 *    * Neither the name of Google Inc. nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package io.grpc.transport;

import io.grpc.Metadata;
import io.grpc.Status;

import java.net.HttpURLConnection;

/**
 * Constants for GRPC-over-HTTP (or HTTP/2)
 */
public final class HttpUtil {
  /**
   * The Content-Type header name. Defined here since it is not explicitly defined by the HTTP/2
   * spec.
   */
  public static final Metadata.Key<String> CONTENT_TYPE =
      Metadata.Key.of("content-type", Metadata.ASCII_STRING_MARSHALLER);

  /**
   * Content-Type used for GRPC-over-HTTP/2.
   */
  public static final String CONTENT_TYPE_GRPC = "application/grpc";

  /**
   * The HTTP method used for GRPC requests.
   */
  public static final String HTTP_METHOD = "POST";

  /**
   * The TE header name. Defined here since it is not explicitly defined by the HTTP/2 spec.
   */
  public static final Metadata.Key<String> TE = Metadata.Key.of("te",
      Metadata.ASCII_STRING_MARSHALLER);

  /**
   * The TE (transport encoding) header for requests over HTTP/2
   */
  public static final String TE_TRAILERS = "trailers";

  /**
   * Maps HTTP error response status codes to transport codes.
   */
  public static Status httpStatusToGrpcStatus(int httpStatusCode) {
    // Specific HTTP code handling.
    switch (httpStatusCode) {
      case HttpURLConnection.HTTP_BAD_REQUEST:  // 400
        return Status.INVALID_ARGUMENT;
      case HttpURLConnection.HTTP_UNAUTHORIZED:  // 401
        return Status.UNAUTHENTICATED;
      case HttpURLConnection.HTTP_FORBIDDEN:  // 403
        return Status.PERMISSION_DENIED;
      case HttpURLConnection.HTTP_NOT_FOUND:  // 404
        return Status.NOT_FOUND;
      case HttpURLConnection.HTTP_CONFLICT:  // 409
        return Status.ABORTED;
      case 416:  // Requested range not satisfiable
        return Status.OUT_OF_RANGE;
      case 429:  // Too many requests
        return Status.RESOURCE_EXHAUSTED;
      case 499:  // Client closed request
        return Status.CANCELLED;
      case HttpURLConnection.HTTP_NOT_IMPLEMENTED:  // 501
        return Status.UNIMPLEMENTED;
      case HttpURLConnection.HTTP_UNAVAILABLE:  // 503
        return Status.UNAVAILABLE;
      case HttpURLConnection.HTTP_GATEWAY_TIMEOUT:  // 504
        return Status.DEADLINE_EXCEEDED;
    }
    // Generic HTTP code handling.
    if (httpStatusCode < 200) {
      // 1xx and below
      return Status.UNKNOWN;
    }
    if (httpStatusCode < 300) {
      // 2xx
      return Status.OK;
    }
    if (httpStatusCode < 400) {
      // 3xx
      return Status.UNKNOWN;
    }
    if (httpStatusCode < 500) {
      // 4xx
      return Status.FAILED_PRECONDITION;
    }
    if (httpStatusCode < 600) {
      // 5xx
      return Status.INTERNAL;
    }
    return Status.UNKNOWN;
  }

  private HttpUtil() {}
}
