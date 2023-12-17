/*
 © 2023 devspacehub, Inc. All rights reserved.

 name : HeaderSettingUtils
 creation : 2023.12.11
 author : Yoonji Moon
 */

package com.devspacehub.ast.util;

import java.net.HttpURLConnection;

/** TODO 삭제 예정
 * The type Header setting utils.
 */
public class HeaderSettingUtils {
    /**
     * Create common header http url connection.
     *
     * @param conn the conn
     * @return the http url connection
     */
    public static HttpURLConnection createCommonHeader(HttpURLConnection conn) {
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("authorization", "Bearer {TOKEN}");
        conn.setRequestProperty("appKey", "{Client_ID}");
        conn.setRequestProperty("appSecret", "{Client_Secret}");
        conn.setRequestProperty("personalSeckey", "{personalSeckey}");
        return conn;
    }
}
