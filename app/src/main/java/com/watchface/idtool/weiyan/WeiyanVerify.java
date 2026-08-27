package com.watchface.idtool.weiyan;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Random;

/**
 * 微验网络验证 - 独立模块（从最新注入代码提取，无 UI）
 *
 * 调用方式：
 *   WeiyanVerify v = new WeiyanVerify(context);
 *   v.setCallback(...);
 *   v.getNotice(); / v.checkUpdate(); / v.login(kami); / v.unbind(kami);
 *
 * 入口保持异步回调，业务层用挂起函数桥接即可。
 */
public class WeiyanVerify {

    // ========================== 接口配置（与最新注入一致） ==========================
    private static final String API_BASE = "https://wy.llua.cn/v2/";
    private static final String API_PATH = "1e75396ffb329147c8bcf76b8e4d76f2";
    private static final String API_URL = API_BASE + API_PATH;

    private static final String ID_NOTICE = "tz8J8TdxNTx";
    private static final String ID_UPDATE = "UNZInZiL1UF";
    private static final String ID_LOGIN = "9b81i8xD6BP";
    private static final String ID_UNBIND = "qkV9sc4fK7Q";

    private static final int CODE_NOTICE_OK = 76536;
    private static final int CODE_UPDATE_OK = 93883;
    private static final int CODE_LOGIN_OK = 27665;
    private static final int CODE_UNBIND_OK = 91947;

    /** 登录/解绑签名盐 */
    private static final String SIGN_SALT = "z31e06d3b903185aedb4e9ba0f4616c8";

    // 请求加密密钥
    private static final String KEY_RC4_REQ_1 = "wa255a4dc81f9f09838a63fb11a21";
    private static final String KEY_RC4_REQ_2 = "e641b334ad5b468b56a";
    private static final String KEY_CUSTOM_B64 =
            "Ig0/pHQidFyeXM73nuYUAj2mwa9ckOzhxDPt6rECNG8vV1LRSKl5BJ4TfoqW+sZb";

    // 公告/更新/解绑 响应解密
    private static final String KEY_RC4_RESP_SIMPLE = "b09f1939f418ca3c8d434652f58d3b65478";

    // 登录响应解密链
    private static final String KEY_RC4_LOGIN_1 = "zc67b217c572de56978";
    private static final String KEY_CB64_LOGIN_1 =
            "Tfca0Je6PW5Qw1VRYLZC83EKSvtMpFO2ygAz+oj9rhxIBH7mb4slUdiqnGukN/DX";
    private static final String KEY_CB64_LOGIN_2 =
            "63nj1GfBmR720wQh9XM+4teOPSdEJok8FIVasTxglHzAZbLNUDKYWci5uyprq/vC";
    private static final String KEY_CB64_LOGIN_3 =
            "0LEz/itrdl65W3FNOK9uUXefhVncZqsADgjHCyBQv7pm1TwRxIY8Mko4a+SPJ2bG";
    private static final String KEY_RC4_LOGIN_2 = "t9f8ab2b2e2c6280ed00d6870ac";

    // 登录成功校验字段
    private static final String FIELD_CODE = "e201896f8a8fb18fa76eeafe71f7db799";
    private static final String FIELD_MSG = "za6e5983651f70247537a6ce2adb9dc16";
    private static final String FIELD_CHECK = "w9675c338b880efb21864589c7b003ca4";
    private static final String CHECK_VALUE = "ad2280f4722dc1a2aeee096561ffc2f7";
    private static final String FIELD_SERVER_TS = "w7c0fe26d8449388e554c87219104e67a";
    private static final String FIELD_SIGN1 = "h7f2b0109";
    private static final String FIELD_SIGN2 = "rf312bcc44a63bc0949";
    private static final String FIELD_SIGN3 = "x4c98f4e0f2d3fafcf6";
    private static final String FIELD_TYPE = "b43b081705882e569f67283f3567ef164";
    private static final String FIELD_REMAIN = "ge3e2e51874874cd9ec006250be249ee4";
    private static final String FIELD_EXPIRE = "aa2edab7368a286d052b2bf0503d00e8a";

    private final Context mContext;
    private final Handler mMainHandler;
    private AuthCallback mCallback;
    private boolean isUp = false;

    public interface AuthCallback {
        void onNotice(String notice);

        void onUpdateCheck(boolean hasUpdate, String version, String updateUrl, String updateShow);

        void onLoginSuccessSingle(int remainCount);

        void onLoginSuccessTime(String expireTime, long expireTimestamp);

        void onLoginFailed(String msg);

        void onUnbind(boolean success, String msg, String remainNum);

        void onError(String apiName, String error);
    }

    public WeiyanVerify(Context context) {
        this.mContext = context.getApplicationContext();
        this.mMainHandler = new Handler(Looper.getMainLooper());
    }

    public void setCallback(AuthCallback callback) {
        this.mCallback = callback;
    }

    public void setForceUpdate(boolean force) {
        this.isUp = force;
    }

    // ========================== 对外 API ==========================

    public void getNotice() {
        new Thread(() -> {
            try {
                String payload = buildRequestPayload("id=" + ID_NOTICE);
                String response = httpPost(API_URL, payload);
                String plain = decryptSimple(response);
                JSONObject json = new JSONObject(plain);
                String notice;
                if (json.getInt("code") == CODE_NOTICE_OK) {
                    notice = json.getJSONObject("msg").getString("app_gg");
                } else {
                    notice = json.optString("msg", plain);
                }
                final String n = notice;
                post(() -> {
                    if (mCallback != null) mCallback.onNotice(n);
                });
            } catch (Exception e) {
                postError("getNotice", safeMsg(e));
            }
        }).start();
    }

    public void checkUpdate() {
        new Thread(() -> {
            try {
                String payload = buildRequestPayload("id=" + ID_UPDATE);
                String response = httpPost(API_URL, payload);
                String plain = decryptSimple(response);
                JSONObject json = new JSONObject(plain);
                if (json.getInt("code") == CODE_UPDATE_OK) {
                    JSONObject msg = json.getJSONObject("msg");
                    String latestVersion = msg.optString("version", "");
                    String currentVersion = getAppVersion();
                    boolean hasUpdate = !latestVersion.isEmpty() && !latestVersion.equals(currentVersion);
                    String updateUrl = msg.optString("updateurl", "");
                    String updateShow = msg.optString("updateshow", "");
                    post(() -> {
                        if (mCallback != null) {
                            mCallback.onUpdateCheck(hasUpdate, latestVersion, updateUrl, updateShow);
                        }
                    });
                } else {
                    postError("checkUpdate", json.optString("msg", "检查更新失败"));
                }
            } catch (Exception e) {
                postError("checkUpdate", safeMsg(e));
            }
        }).start();
    }

    public void login(final String kami) {
        if (kami == null || kami.trim().isEmpty()) {
            postError("login", "卡密不能为空");
            return;
        }
        if (isUp) {
            postError("login", "强制更新，请先更新后使用");
            return;
        }
        final String card = kami.trim();
        new Thread(() -> {
            try {
                String deviceId = getDeviceId();
                long timestamp = System.currentTimeMillis() / 1000;
                long randomVal = randomInt(10000, 999999);
                String sign = md5(
                        "kami=" + card
                                + "&markcode=" + deviceId
                                + "&t=" + timestamp
                                + "&" + SIGN_SALT
                );
                String raw = "id=" + ID_LOGIN
                        + "&kami=" + card
                        + "&markcode=" + deviceId
                        + "&t=" + timestamp
                        + "&sign=" + sign
                        + "&value=" + randomVal;
                String payload = buildRequestPayload(raw);
                String response = httpPost(API_URL, payload);
                String plain = decryptLogin(response);
                JSONObject json = new JSONObject(plain);

                int code = json.getInt(FIELD_CODE);
                if (code == CODE_LOGIN_OK
                        && json.getJSONObject(FIELD_MSG).getString(FIELD_CHECK).equals(CHECK_VALUE)) {
                    long serverTs = json.getLong(FIELD_SERVER_TS);
                    if (serverTs - timestamp > 30 || serverTs - timestamp < -30) {
                        post(() -> {
                            if (mCallback != null) mCallback.onLoginFailed("设备时间不准");
                        });
                        return;
                    }
                    JSONObject msg = json.getJSONObject(FIELD_MSG);
                    // 签名校验
                    String s1 = msg.getString(FIELD_SIGN1);
                    String s2 = msg.getString(FIELD_SIGN2);
                    String s3 = msg.getString(FIELD_SIGN3);
                    String calc1 = sha256(sha1("" + sign + "" + randomVal + ""));
                    String calc2 = sha1(sha256("" + randomVal + "" + SIGN_SALT + ""));
                    String calc3 = md5(md5(sha1(
                            "" + code + "" + timestamp + "" + code + "" + serverTs + ""
                    )));
                    if (!s1.equals(calc1) || !s2.equals(calc2) || !s3.equals(calc3)) {
                        post(() -> {
                            if (mCallback != null) mCallback.onLoginFailed("校验失败");
                        });
                        return;
                    }
                    String type = msg.optString(FIELD_TYPE, "");
                    if ("single".equals(type)) {
                        int remain = 0;
                        try {
                            remain = Integer.parseInt(msg.optString(FIELD_REMAIN, "0"));
                        } catch (Exception ignored) {
                        }
                        final int r = remain;
                        post(() -> {
                            if (mCallback != null) mCallback.onLoginSuccessSingle(r);
                        });
                    } else {
                        long expireTs = msg.optLong(FIELD_EXPIRE, 0L);
                        String expireTime = formatTs(expireTs);
                        post(() -> {
                            if (mCallback != null) mCallback.onLoginSuccessTime(expireTime, expireTs);
                        });
                    }
                } else {
                    String err;
                    try {
                        err = json.getString(FIELD_MSG);
                    } catch (Exception e) {
                        try {
                            err = json.getJSONObject(FIELD_MSG).toString();
                        } catch (Exception e2) {
                            err = "登录失败";
                        }
                    }
                    final String msg = err;
                    post(() -> {
                        if (mCallback != null) mCallback.onLoginFailed(msg);
                    });
                }
            } catch (Exception e) {
                postError("login", safeMsg(e));
            }
        }).start();
    }

    public void unbind(final String kami) {
        if (kami == null || kami.trim().isEmpty()) {
            postError("unbind", "卡密不能为空");
            return;
        }
        final String card = kami.trim();
        new Thread(() -> {
            try {
                String deviceId = getDeviceId();
                long timestamp = System.currentTimeMillis() / 1000;
                long randomVal = randomInt(10000, 999999);
                String sign = md5(
                        "kami=" + card
                                + "&markcode=" + deviceId
                                + "&t=" + timestamp
                                + "&" + SIGN_SALT
                );
                String raw = "id=" + ID_UNBIND
                        + "&kami=" + card
                        + "&markcode=" + deviceId
                        + "&t=" + timestamp
                        + "&sign=" + sign
                        + "&value=" + randomVal;
                String payload = buildRequestPayload(raw);
                String response = httpPost(API_URL, payload);
                String plain = decryptSimple(response);
                JSONObject json = new JSONObject(plain);
                if (json.getInt("code") == CODE_UNBIND_OK) {
                    String num = json.getJSONObject("msg").optString("num", "");
                    post(() -> {
                        if (mCallback != null) {
                            mCallback.onUnbind(true, "解绑成功", num);
                        }
                    });
                } else {
                    String err = json.optString("msg", "解绑失败");
                    post(() -> {
                        if (mCallback != null) mCallback.onUnbind(false, err, "");
                    });
                }
            } catch (Exception e) {
                postError("unbind", safeMsg(e));
            }
        }).start();
    }

    // ========================== 请求加密链（与注入一致） ==========================
    /**
     * 原始参数 → stdB64 → hex → RC4(key1) → hex → stdB64 → RC4(key2) → hex → stdB64 → customB64
     */
    private String buildRequestPayload(String rawParams) {
        String s1 = standardBase64Encode(rawParams);
        String s2 = stringToHex(s1);
        byte[] s3 = rc4(s2.getBytes(StandardCharsets.UTF_8), KEY_RC4_REQ_1);
        String s4 = bytesToHex(s3);
        String s5 = standardBase64Encode(s4);
        byte[] s6 = rc4(s5.getBytes(StandardCharsets.UTF_8), KEY_RC4_REQ_2);
        String s7 = bytesToHex(s6);
        String s8 = standardBase64Encode(s7);
        return customBase64Encode(s8, KEY_CUSTOM_B64);
    }

    /** 公告/更新/解绑：hex → RC4 */
    private String decryptSimple(String response) throws Exception {
        String hex = sanitizeHex(response);
        byte[] bytes = hexToBytes(hex);
        return new String(rc4(bytes, KEY_RC4_RESP_SIMPLE), StandardCharsets.UTF_8);
    }

    /**
     * 登录：hex→RC4 → customB64×3 → hex→RC4
     */
    private String decryptLogin(String response) throws Exception {
        String hex = sanitizeHex(response);
        byte[] b1 = hexToBytes(hex);
        String s1 = new String(rc4(b1, KEY_RC4_LOGIN_1), StandardCharsets.UTF_8);
        String s2 = customBase64Decode(s1, KEY_CB64_LOGIN_1);
        String s3 = customBase64Decode(s2, KEY_CB64_LOGIN_2);
        String s4 = customBase64Decode(s3, KEY_CB64_LOGIN_3);
        String hex2 = sanitizeHex(s4);
        byte[] b2 = hexToBytes(hex2);
        return new String(rc4(b2, KEY_RC4_LOGIN_2), StandardCharsets.UTF_8);
    }

    /**
     * 清洗响应：去掉空白；若不是纯十六进制（例如以 [ 或 { 开头的明文 JSON/错误页），抛出可读异常，
     * 避免 Integer.parseInt 抛出 "For input string: \"[\" under radix 16"。
     */
    private static String sanitizeHex(String raw) throws Exception {
        if (raw == null) {
            throw new Exception("服务器返回为空");
        }
        String s = raw.trim().replaceAll("\\s+", "");
        if (s.isEmpty()) {
            throw new Exception("服务器返回为空");
        }
        // 明文 JSON / HTML 等
        char c0 = s.charAt(0);
        if (c0 == '[' || c0 == '{' || c0 == '<' || c0 == '"') {
            String preview = s.length() > 120 ? s.substring(0, 120) + "..." : s;
            throw new Exception("服务器返回非加密数据: " + preview);
        }
        // 只保留 0-9a-fA-F
        StringBuilder sb = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if ((ch >= '0' && ch <= '9')
                    || (ch >= 'a' && ch <= 'f')
                    || (ch >= 'A' && ch <= 'F')) {
                sb.append(ch);
            }
        }
        String hex = sb.toString();
        if (hex.isEmpty()) {
            String preview = s.length() > 80 ? s.substring(0, 80) + "..." : s;
            throw new Exception("响应中无有效十六进制: " + preview);
        }
        if (hex.length() % 2 != 0) {
            hex = "0" + hex;
        }
        return hex;
    }

    // ========================== 工具方法 ==========================

    private void post(Runnable r) {
        mMainHandler.post(r);
    }

    private void postError(String api, String error) {
        post(() -> {
            if (mCallback != null) mCallback.onError(api, error);
        });
    }

    private static String safeMsg(Exception e) {
        String m = e.getMessage();
        if (m == null || m.isEmpty()) return e.toString();
        // 把经典 radix 16 错误转成可读信息
        if (m.contains("under radix 16") || m.contains("For input string")) {
            return "响应解密失败（数据格式异常）: " + m;
        }
        return m;
    }

    private String getDeviceId() {
        try {
            String id = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
            return id != null ? id : "unknown";
        } catch (Exception e) {
            return "unknown";
        }
    }

    private String getAppVersion() {
        try {
            PackageInfo pi = mContext.getPackageManager()
                    .getPackageInfo(mContext.getPackageName(), 0);
            return pi.versionName != null ? pi.versionName : "";
        } catch (PackageManager.NameNotFoundException e) {
            return "";
        }
    }

    private static int randomInt(int min, int max) {
        return new Random().nextInt((max - min) + 1) + min;
    }

    private static String formatTs(long ts) {
        if (ts <= 0) return "";
        try {
            GregorianCalendar gc = new GregorianCalendar();
            gc.setTimeInMillis(ts < 1_000_000_000_000L ? ts * 1000 : ts);
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(gc.getTime());
        } catch (Exception e) {
            return String.valueOf(ts);
        }
    }

    private static String httpPost(String urlStr, String body) throws Exception {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            byte[] data = body.getBytes(StandardCharsets.UTF_8);
            conn.setFixedLengthStreamingMode(data.length);
            OutputStream os = conn.getOutputStream();
            os.write(data);
            os.flush();
            os.close();
            int code = conn.getResponseCode();
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    code >= 400 ? conn.getErrorStream() : conn.getInputStream(),
                    StandardCharsets.UTF_8
            ));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();
            if (code >= 400) {
                throw new Exception("HTTP " + code + ": " + sb);
            }
            return sb.toString();
        } finally {
            if (conn != null) conn.disconnect();
        }
    }

    private static String md5(String str) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(str.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(md5.digest());
        } catch (Exception e) {
            return "";
        }
    }

    private static String sha1(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            return bytesToHex(digest.digest(input.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return bytesToHex(digest.digest(input.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static final char[] STD_B64 =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();

    private static String standardBase64Encode(String input) {
        byte[] data = input.getBytes(StandardCharsets.UTF_8);
        StringBuilder encoded = new StringBuilder();
        for (int i = 0; i < data.length; i += 3) {
            int block = 0;
            int remaining = Math.min(3, data.length - i);
            for (int j = 0; j < remaining; j++) {
                block |= (data[i + j] & 0xFF) << (16 - 8 * j);
            }
            for (int j = 0; j < 4; j++) {
                if (j < (remaining + 1)) {
                    encoded.append(STD_B64[(block >> (18 - 6 * j)) & 0x3F]);
                } else {
                    encoded.append('=');
                }
            }
        }
        return encoded.toString();
    }

    private static String customBase64Encode(String input, String key) {
        byte[] data = input.getBytes(StandardCharsets.UTF_8);
        char[] table = key.toCharArray();
        StringBuilder encoded = new StringBuilder();
        for (int i = 0; i < data.length; i += 3) {
            int block = 0;
            int remaining = Math.min(3, data.length - i);
            for (int j = 0; j < remaining; j++) {
                block |= (data[i + j] & 0xFF) << (16 - 8 * j);
            }
            for (int j = 0; j < 4; j++) {
                if (j < (remaining + 1)) {
                    encoded.append(table[(block >> (18 - 6 * j)) & 0x3F]);
                } else {
                    encoded.append('=');
                }
            }
        }
        return encoded.toString();
    }

    private static String customBase64Decode(String input, String key) {
        int[] index = new int[128];
        char[] table = key.toCharArray();
        for (int i = 0; i < table.length; i++) {
            if (table[i] < 128) index[table[i]] = i;
        }
        int length = input.length();
        int padding = 0;
        if (length >= 1 && input.charAt(length - 1) == '=') padding++;
        if (length >= 2 && input.charAt(length - 2) == '=') padding++;
        int byteLength = (length * 6) / 8 - padding;
        if (byteLength < 0) byteLength = 0;
        byte[] decoded = new byte[byteLength];
        int dataIndex = 0;
        int buffer = 0;
        int bitsLeft = 0;
        for (int i = 0; i < length; i++) {
            char ch = input.charAt(i);
            if (ch == '=') break;
            if (ch >= 128) continue;
            int value = index[ch];
            buffer = (buffer << 6) | value;
            bitsLeft += 6;
            if (bitsLeft >= 8) {
                if (dataIndex < decoded.length) {
                    decoded[dataIndex++] = (byte) ((buffer >> (bitsLeft - 8)) & 0xFF);
                }
                bitsLeft -= 8;
            }
        }
        return new String(decoded, StandardCharsets.UTF_8);
    }

    private static String stringToHex(String str) {
        return bytesToHex(str.getBytes(StandardCharsets.UTF_8));
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            String hex = Integer.toHexString(b & 0xFF);
            if (hex.length() < 2) sb.append('0');
            sb.append(hex);
        }
        return sb.toString();
    }

    private static byte[] hexToBytes(String inHex) {
        int hexlen = inHex.length();
        byte[] result = new byte[hexlen / 2];
        for (int i = 0, j = 0; i < hexlen; i += 2, j++) {
            result[j] = (byte) Integer.parseInt(inHex.substring(i, i + 2), 16);
        }
        return result;
    }

    private static byte[] rc4(byte[] input, String mKey) {
        byte[] bkey = mKey.getBytes(StandardCharsets.UTF_8);
        byte[] state = new byte[256];
        for (int i = 0; i < 256; i++) state[i] = (byte) i;
        int index1 = 0, index2 = 0;
        for (int i = 0; i < 256; i++) {
            index2 = ((bkey[index1] & 0xff) + (state[i] & 0xff) + index2) & 0xff;
            byte tmp = state[i];
            state[i] = state[index2];
            state[index2] = tmp;
            index1 = (index1 + 1) % bkey.length;
        }
        int x = 0, y = 0;
        byte[] result = new byte[input.length];
        for (int i = 0; i < input.length; i++) {
            x = (x + 1) & 0xff;
            y = ((state[x] & 0xff) + y) & 0xff;
            byte tmp = state[x];
            state[x] = state[y];
            state[y] = tmp;
            int xorIndex = ((state[x] & 0xff) + (state[y] & 0xff)) & 0xff;
            result[i] = (byte) (input[i] ^ state[xorIndex]);
        }
        return result;
    }
}
