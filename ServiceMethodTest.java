/*
 * Copyright (C) 2013 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package retrofit2;

import java.util.Set;
import okhttp3.ResponseBody;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;
import retrofit2.ServiceMethod;
import retrofit2.Retrofit;

import static org.junit.Assert.fail;
import static retrofit2.RequestBuilderTest.buildRequest;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;

public final class ServiceMethodTest {
  @Test public void pathParameterParsing() throws Exception {
    expectParams("/");
    expectParams("/foo");
    expectParams("/foo/bar");
    expectParams("/foo/bar/{}");
    expectParams("/foo/bar/{taco}", "taco");
    expectParams("/foo/bar/{t}", "t");
    expectParams("/foo/bar/{!!!}/"); // Invalid parameter.
    expectParams("/foo/bar/{}/{taco}", "taco");
    expectParams("/foo/bar/{taco}/or/{burrito}", "taco", "burrito");
    expectParams("/foo/bar/{taco}/or/{taco}", "taco");
    expectParams("/foo/bar/{taco-shell}", "taco-shell");
    expectParams("/foo/bar/{taco_shell}", "taco_shell");
    expectParams("/foo/bar/{sha256}", "sha256");
    expectParams("/foo/bar/{TACO}", "TACO");
    expectParams("/foo/bar/{taco}/{tAco}/{taCo}", "taco", "tAco", "taCo");
    expectParams("/foo/bar/{1}"); // Invalid parameter, name cannot start with digit.
  }
  //comp490 Daniel Parker
  
  @Test public void throwCorrectError() throws Exception{
    class Example {
      @Multipart //
      @FormUrlEncoded //
      @POST("/") //
      Call<ResponseBody> method() {
        return null;
      }
    }
    try {
      buildRequest(Example.class);
      fail();
    } catch (IllegalArgumentException e) {
      assertThat(e).hasMessage(
          "Only one encoding annotation is allowed.\n    for method Example.method");
    }   
  }
  
  private static void expectParams(String path, String... expected) {
    Set<String> calculated = ServiceMethod.parsePathParameters(path);
    assertThat(calculated).containsExactly(expected);
  }
}
