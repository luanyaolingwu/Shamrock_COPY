package tencent.mobileim.structmsg;

import com.tencent.mobileqq.pb.ByteStringMicro;
import com.tencent.mobileqq.pb.MessageMicro;
import com.tencent.mobileqq.pb.PBBytesField;
import com.tencent.mobileqq.pb.PBEnumField;
import com.tencent.mobileqq.pb.PBField;
import com.tencent.mobileqq.pb.PBRepeatMessageField;
import com.tencent.mobileqq.pb.PBStringField;
import com.tencent.mobileqq.pb.PBUInt32Field;
import com.tencent.mobileqq.pb.PBUInt64Field;

public final class structmsg$RspNextSystemMsg extends MessageMicro<structmsg$RspNextSystemMsg> {
  static final FieldMap __fieldMap__;
  
  public final PBStringField bytes_game_nick = PBField.initString("");
  
  public final PBBytesField bytes_undecid_for_qim = PBField.initBytes(ByteStringMicro.EMPTY);
  
  public final PBEnumField checktype = PBField.initEnum(1);
  
  public final PBUInt64Field following_friend_seq = PBField.initUInt64(0L);
  
  public final PBUInt64Field following_group_seq = PBField.initUInt64(0L);
  
  public structmsg$RspHead head = new structmsg$RspHead();
  
  public final PBRepeatMessageField<structmsg$StructMsg> msgs = PBField.initRepeatMessage(structmsg$StructMsg.class);
  
  public final PBUInt32Field uint32_un_read_count3 = PBField.initUInt32(0);
  
  static {
    Long long_ = Long.valueOf(0L);
    ByteStringMicro byteStringMicro = ByteStringMicro.EMPTY;
    __fieldMap__ = MessageMicro.initFieldMap(new int[] { 10, 18, 24, 32, 40, 802, 810, 816 }, new String[] { "head", "msgs", "following_friend_seq", "following_group_seq", "checktype", "bytes_game_nick", "bytes_undecid_for_qim", "uint32_un_read_count3" }, new Object[] { null, null, long_, long_, Integer.valueOf(1), "", byteStringMicro, Integer.valueOf(0) }, structmsg$RspNextSystemMsg.class);
  }
}
