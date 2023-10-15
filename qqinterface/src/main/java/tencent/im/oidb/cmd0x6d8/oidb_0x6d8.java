package tencent.im.oidb.cmd0x6d8;

import com.tencent.mobileqq.pb.MessageMicro;
import com.tencent.mobileqq.pb.PBBoolField;
import com.tencent.mobileqq.pb.PBField;
import com.tencent.mobileqq.pb.PBInt32Field;
import com.tencent.mobileqq.pb.PBStringField;
import com.tencent.mobileqq.pb.PBUInt32Field;
import com.tencent.mobileqq.pb.PBUInt64Field;

public class oidb_0x6d8 {
    public static class ReqBody extends MessageMicro<ReqBody> {
        //static final MessageMicro.FieldMap __fieldMap__ = MessageMicro.initFieldMap(new int[]{10, 18, 26, 34, 42}, new String[]{"file_info_req", "file_list_info_req", "group_file_cnt_req", "group_space_req", "file_preview_req"}, new Object[]{null, null, null, null, null}, oidb_0x6d8$ReqBody.class);
        //public oidb_0x6d8$GetFileInfoReqBody file_info_req = new oidb_0x6d8$GetFileInfoReqBody();
        //public oidb_0x6d8$GetFileListReqBody file_list_info_req = new oidb_0x6d8$GetFileListReqBody();
        public GetFileCountReqBody group_file_cnt_req = new GetFileCountReqBody();
        public GetSpaceReqBody group_space_req = new GetSpaceReqBody();
        //public oidb_0x6d8$GetFilePreviewReqBody file_preview_req = new oidb_0x6d8$GetFilePreviewReqBody();
    }

    public static final class GetFileCountReqBody extends MessageMicro<GetFileCountReqBody> {
        public final PBUInt64Field uint64_group_code = PBField.initUInt64(0);
        public final PBUInt32Field uint32_app_id = PBField.initUInt32(0);
        public final PBUInt32Field uint32_bus_id = PBField.initUInt32(0);
    }

    public static class RspBody extends MessageMicro<RspBody> {
        //static final MessageMicro.FieldMap __fieldMap__ = MessageMicro.initFieldMap(new int[]{10, 18, 26, 34, 42}, new String[]{"file_info_rsp", "file_list_info_rsp", "group_file_cnt_rsp", "group_space_rsp", "file_preview_rsp"}, new Object[]{null, null, null, null, null}, oidb_0x6d8$RspBody.class);
        //public oidb_0x6d8$GetFileInfoRspBody file_info_rsp = new oidb_0x6d8$GetFileInfoRspBody();
        //public oidb_0x6d8$GetFileListRspBody file_list_info_rsp = new oidb_0x6d8$GetFileListRspBody(); // 2
        public GetFileCountRspBody group_file_cnt_rsp = new GetFileCountRspBody(); // 3
        public GetSpaceRspBody group_space_rsp = new GetSpaceRspBody(); // 4
        //public oidb_0x6d8$GetFilePreviewRspBody file_preview_rsp = new oidb_0x6d8$GetFilePreviewRspBody();
    }

    public static class GetSpaceRspBody extends MessageMicro<GetSpaceRspBody> {
        //static final MessageMicro.FieldMap __fieldMap__ = MessageMicro.initFieldMap(new int[]{8, 18, 26, 32, 40, 48}, new String[]{"int32_ret_code", "str_ret_msg", "str_client_wording", "uint64_total_space", "uint64_used_space", "bool_all_upload"}, new Object[]{0, "", "", 0L, 0L, Boolean.FALSE}, oidb_0x6d8$GetSpaceRspBody.class);
        public final PBInt32Field int32_ret_code = PBField.initInt32(0);
        public final PBStringField str_ret_msg = PBField.initString("");
        public final PBStringField str_client_wording = PBField.initString("");
        public final PBUInt64Field uint64_total_space = PBField.initUInt64(0);
        public final PBUInt64Field uint64_used_space = PBField.initUInt64(0);
        public final PBBoolField bool_all_upload = PBField.initBool(false);
    }

    public static class GetFileCountRspBody extends MessageMicro<GetFileCountRspBody> {
        public final PBInt32Field int32_ret_code = PBField.initInt32(0);
        public final PBStringField str_ret_msg = PBField.initString("");
        public final PBStringField str_client_wording = PBField.initString("");
        public final PBUInt32Field uint32_all_file_count = PBField.initUInt32(0);
        public final PBBoolField bool_file_too_many = PBField.initBool(false);
        public final PBUInt32Field uint32_limit_count = PBField.initUInt32(0);
        public final PBBoolField bool_is_full = PBField.initBool(false);
    }

    public static class GetSpaceReqBody extends MessageMicro<GetSpaceReqBody> {
        //static final MessageMicro.FieldMap __fieldMap__ = MessageMicro.initFieldMap(new int[]{8, 16}, new String[]{"uint64_group_code", "uint32_app_id"}, new Object[]{0L, 0}, oidb_0x6d8$GetSpaceReqBody.class);
        public final PBUInt64Field uint64_group_code = PBField.initUInt64(0);
        public final PBUInt32Field uint32_app_id = PBField.initUInt32(0);
    }

}
