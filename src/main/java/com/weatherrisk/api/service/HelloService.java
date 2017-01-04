/*
 * Copyright 2015 Stormpath, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.weatherrisk.api.service;

import com.stormpath.sdk.account.Account;
import com.weatherrisk.api.cnst.StormPathCnst;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

/**
 * @since 1.0.RC5
 */
@Service
public class HelloService {

    /**
     * Only users who have a Custom Data entry in their Stormpath Account or Group containing something like
     * <code>"springSecurityPermissions":["say:*"]</code> or <code>"springSecurityPermissions":["say:hello"]</code>
     * will be allowed to execute this method.
     */
    @PreAuthorize("hasAuthority('" + StormPathCnst.GROUP_ADMIN_URL + "') and hasPermission('say', 'hello')")
    public String sayHello(Account account) {
        return "Hello " + account.getGivenName() + "! You have the required permissions to access this restricted resource.";
    }

}
