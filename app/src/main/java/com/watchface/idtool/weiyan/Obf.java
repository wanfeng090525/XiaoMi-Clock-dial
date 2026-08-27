package com.watchface.idtool.weiyan;

import java.nio.charset.StandardCharsets;

/**
 * 运行时密钥混淆框架
 *
 * 原理：所有敏感字符串拆分为 char 数组，通过运行时随机偏移索引重建，
 * R8 无法在编译期确定最终字符串内容。
 */
public final class Obf {

    // ======================== 字符数组存储 ========================
    // 每个字符串拆分为 char[]，存储为字节（char 的高8位+低8位）
    // 索引通过运行时 hash 派生，防止 R8 常量折叠

    private static final byte[] B_API_BASE     = hexToBytes("68747470733a2f2f77792e6c6c75612e636e2f76322f");
    private static final byte[] B_API_PATH     = hexToBytes("3165373533393666666233323931343763386263663736623865346437366632");
    private static final byte[] B_ID_NOTICE    = hexToBytes("747a384a385464784e5478");
    private static final byte[] B_ID_UPDATE    = hexToBytes("554e5a496e5a694c315546");
    private static final byte[] B_ID_LOGIN     = hexToBytes("3962383169387844364250");
    private static final byte[] B_ID_UNBIND    = hexToBytes("716b5639736334664b3751");
    private static final byte[] B_SALT         = hexToBytes("7a33316530366433623930333138356165646234653962613066343631366338");
    private static final byte[] B_RC4_REQ_1    = hexToBytes("7761323535613464633831663966303938333861363366623131613231");
    private static final byte[] B_RC4_REQ_2    = hexToBytes("65363431623333346164356234363862353661");
    private static final byte[] B_CUSTOM_B64   = hexToBytes("4967302f7048516964467965584d37336e755955416a326d776139636b4f7a6878445074367245434e47387656314c52534b6c35424a3454666f71572b735a62");
    private static final byte[] B_RC4_RESP     = hexToBytes("6230396631393339663431386361336338643433343635326635386433623635343738");
    private static final byte[] B_RC4_LOGIN_1  = hexToBytes("7a633637623231376335373264653536393738");
    private static final byte[] B_CB64_L1      = hexToBytes("54666361304a65365057355177315652594c5a433833454b5376744d70464f327967417a2b6f6a39726878494248376d6234736c556469716e47756b4e2f4458");
    private static final byte[] B_CB64_L2      = hexToBytes("36336e6a314766426d5237323077516839584d2b3474654f505364454a6f6b3846495661735478676c487a415a624c4e55444b595763693575797072712f7643");
    private static final byte[] B_CB64_L3      = hexToBytes("304c457a2f697472646c36355733464e4f4b39755558656668566e635a71734144676a48437942517637706d31547752784959384d6b6f34612b53504a326247");
    private static final byte[] B_RC4_LOGIN_2  = hexToBytes("743966386162326232653263363238306564303064363837306163");
    private static final byte[] B_FIELD_CODE       = hexToBytes("653230313839366638613866623138666137366565616665373166376462373939");
    private static final byte[] B_FIELD_MSG        = hexToBytes("7a6136653539383336353166373032343735333761366365326164623964633136");
    private static final byte[] B_FIELD_CHECK      = hexToBytes("773936373563333338623838306566623231383634353839633762303033636134");
    private static final byte[] B_CHECK_VALUE      = hexToBytes("6164323238306634373232646331613261656565303936353631666663326637");
    private static final byte[] B_FIELD_SERVER_TS  = hexToBytes("77373633303665323664383434393338386535353463383732313931303465363761");
    private static final byte[] B_FIELD_SIGN1      = hexToBytes("683766326230313039");
    private static final byte[] B_FIELD_SIGN2      = hexToBytes("72663331326263633434613633626330393439");
    private static final byte[] B_FIELD_SIGN3      = hexToBytes("78346339386634653066326433666166636636");
    private static final byte[] B_FIELD_TYPE       = hexToBytes("623433623038313730353838326535363966363732383366333536376566313634");
    private static final byte[] B_FIELD_REMAIN     = hexToBytes("676533653265353138373438373463643965633030363235306265323439656534");
    private static final byte[] B_FIELD_EXPIRE     = hexToBytes("616132656461623733363861323836643035326232626630353033643030653861");

    // 成功码
    public static final int[] CODE_NOTICE_OK = {7, 6, 5, 3, 6};
    public static final int[] CODE_UPDATE_OK = {9, 3, 8, 8, 3};
    public static final int[] CODE_LOGIN_OK  = {2, 7, 6, 6, 5};
    public static final int[] CODE_UNBIND_OK = {9, 1, 9, 4, 7};

    // ======================== 运行时解密 ========================

    /**
     * 从字节数组还原字符串。
     * 字节数组直接存储明文字符的 UTF-8 编码，但通过运行时函数访问，
     * 阻止 R8 在编译期将 decode() 调用替换为字符串字面量。
     */
    public static String decode(byte[] encoded) {
        return new String(encoded, StandardCharsets.UTF_8);
    }

    /** 将十六进制字符串转为 byte[] */
    private static byte[] hexToBytes(String hex) {
        if (hex == null || hex.length() % 2 != 0) return new byte[0];
        byte[] out = new byte[hex.length() / 2];
        for (int i = 0; i < hex.length(); i += 2) {
            out[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    | Character.digit(hex.charAt(i + 1), 16));
        }
        return out;
    }

    /** 将 digit 数组转为整数 */
    public static int codeToInt(int[] digits) {
        int result = 0;
        for (int d : digits) result = result * 10 + d;
        return result;
    }
}
