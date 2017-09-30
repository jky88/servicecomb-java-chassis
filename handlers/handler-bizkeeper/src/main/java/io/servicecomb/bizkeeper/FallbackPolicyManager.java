/*
 * Copyright 2017 Huawei Technologies Co., Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.servicecomb.bizkeeper;

import java.util.HashMap;
import java.util.Map;

import io.servicecomb.core.Invocation;
import io.servicecomb.swagger.invocation.Response;

public class FallbackPolicyManager {
  private static final Map<String, FallbackPolicy> POLICIES = new HashMap<>();

  public static void addPolicy(FallbackPolicy policy) {
    POLICIES.put(policy.name(), policy);
  }

  public static void record(String type, Invocation invocation, Response response, boolean isSuccess) {
    FallbackPolicy policy = POLICIES.get(Configuration.INSTANCE.getFallbackPolicyPolicy(type,
        invocation.getMicroserviceName(),
        invocation.getOperationMeta().getMicroserviceQualifiedName()));
    if (policy != null) {
      policy.record(invocation, response, isSuccess);
    }
  }

  public static Response getFallbackResponse(String type, Invocation invocation) {
    FallbackPolicy policy = POLICIES.get(Configuration.INSTANCE.getFallbackPolicyPolicy(type,
        invocation.getMicroserviceName(),
        invocation.getOperationMeta().getMicroserviceQualifiedName()));
    if (policy != null) {
      return policy.getFallbackResponse(invocation);
    } else {
      return Response.failResp(invocation.getInvocationType(),
          BizkeeperExceptionUtils
          .createBizkeeperException(BizkeeperExceptionUtils.CSE_HANDLER_BK_FALLBACK,
              null,
              invocation.getOperationMeta().getMicroserviceQualifiedName()));
    }
  }

}
