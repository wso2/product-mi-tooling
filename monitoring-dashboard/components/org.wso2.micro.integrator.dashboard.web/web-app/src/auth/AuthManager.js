/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import {Constants} from './Constants';
import qs from 'qs'
import axios from 'axios'

export default class AuthManager {

    /**
     * Authenticate the user and set the user into the session.
     *
     * @param {string} username Username
     * @param {string } password Password
     * @returns {Promise} Promise
     */
    static authenticate(username, password, isSecure = true) {

        return new Promise((resolve, reject) => {
            axios({
                method: 'post',
                url: AuthManager.getBasePath().concat('/login'),
                data: qs.stringify({
                  username: username,
                  password: password
                }),
                headers: {
                  'content-type': 'application/x-www-form-urlencoded;charset=utf-8'
                }
            })
            .then((response) => {
                AuthManager.setUser(response.data, isSecure);
                resolve();
            })
            .catch(error => {
                reject(error);
            });
        }); 
    }

    /**
     * Check whether the user is logged in.
     *
     * @returns {boolean} Status
     */
    static isLoggedIn() {
        return !!AuthManager.getUser();
    }

    /**
     * Get user from the session cookie.
     *
     * @returns {{}|null} User object
     */
    static getUser() {
        const buffer = AuthManager.getCookie(Constants.SESSION_USER_COOKIE);
        return buffer ? JSON.parse(buffer) : null;
    }

    /**
     * Set user into a session cookie.
     *
     * @param {{}} user  User object
     */
    static setUser(user, isSecure) {
        // secure user cookie will only be set in https mode
        AuthManager.setCookie(Constants.SESSION_USER_COOKIE, JSON.stringify(user)+";SameSite=Strict", 3000, window.contextPath, isSecure);
    }

    /**
     * Discard active user session.
     */
    static discardSession() {
        AuthManager.deleteCookie(Constants.SESSION_USER_COOKIE);
        window.localStorage.clear();
    }

    /**
     * Delete a browser cookie given its name
     * @param {String} name : Name of the cookie which need to be deleted
     */
    static deleteCookie(name) {
        document.cookie = name + '=; path=' + window.contextPath + '; expires=Thu, 01 Jan 1970 00:00:01 GMT;';
    }

    /**
     * Calculate expiry time.
     *
     * @param validityPeriod
     * @returns {Date}
     */
    static calculateExpiryTime(validityPeriod) {
        let expires = new Date();
        expires.setSeconds(expires.getSeconds() + validityPeriod);
        return expires;
    }

    /**
     * Logout user by revoking tokens and clearing the session.
     *
     * @returns {Promise} Promise
     */
    static logout() {
        return new Promise((resolve, reject) => {
            axios({
                method: 'get',
                url: AuthManager.getBasePath().concat('/logout')
            })
            .then(() => {
                AuthManager.discardSession();
                resolve();
            })
            .catch(error => reject(error));
        });
    }

    /**
     * Set a cookie with given name and value assigned to it.
     * @param {String} name : Name of the cookie which need to be set
     * @param {String} value : Value of the cookie, expect it to be URLEncoded
     * @param {number} validityPeriod :  (Optional) Validity period of the cookie in seconds
     * @param {String} path : Path which needs to set the given cookie
     * @param {boolean} secured : secured parameter is set
     */
    static setCookie(name, value, validityPeriod, path = "/", secured = true) {
        let expires = '';
        const securedDirective = secured ? "; Secure" : "";
        if (validityPeriod) {
            const date = new Date();
            date.setTime(date.getTime() + validityPeriod * 1000);
            expires = "; expires=" + date.toUTCString();
        }
        document.cookie = name + "=" + value + expires + "; path=" + path + securedDirective;
    }

    /**
     * Get JavaScript accessible cookies saved in browser, by giving the cooke name.
     * @param {String} name : Name of the cookie which need to be retrived
     * @returns {String|null} : If found a cookie with given name , return its value,Else null value is returned
     */
    static getCookie(name) {
        name = `${name}=`;
        const arr = document.cookie.split(';');
        for (let i = 0; i < arr.length; i++) {
            let c = arr[i];
            while (c.charAt(0) === ' ') {
                c = c.substring(1);
            }
            if (c.indexOf(name) === 0) {
                return c.substring(name.length, c.length);
            }
        }
        return '';
    }

    static getBasePath() {
        return window.location.protocol+'//'+window.location.hostname+(window.location.port ? ':'+window.location.port: '') + "/dashboard/api";
    }
}
