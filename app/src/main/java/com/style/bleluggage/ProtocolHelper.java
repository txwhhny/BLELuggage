package com.style.bleluggage;

import android.support.annotation.Nullable;
import org.jetbrains.annotations.Contract;

import java.util.Arrays;


/**
 * Created by Administrator on 2016/11/10.
 */

public class ProtocolHelper {

    public class OnQueryReplyListener
    {
        public void onSpeedGot(int iSpeed){}
        public void onAngleGot(int iAngle){}
        public void onMaxSpeedGot(int iMaxSpeed){}
        public void onAlarmRadGot(int iAlarmRad){}
        public void onAlarmTelGot(String strTelephone){}
        public void onWeightNetGot(int iWeightNet){}
        public void onWeightGrossGot(int iWeightGross){}
        public void onPowerGot(int iPower){}
        public void onMileageGot(int iMileage){}
    }

    public class OnSetReplyListener
    {
        public void onSetReply(String strObject){}
    }

    private static final byte FRAME_FLAG = (byte)0xFF;

    private static final byte OP_QUERY_REQ = 0x00;
    private static final byte OP_QUERY_REPLY= 0x01;
    private static final byte OP_SET_REQ = 0x02;
    private static final byte OP_SET_REPLY = 0x03;
    private static final byte OP_SET = 0x04;

    public static final byte OBJ_SPEED = 0x01;
    public static final byte OBJ_ANGLE = 0x02;
    public static final byte OBJ_MAX_SPEED= 0x03;
    public static final byte OBJ_ALARM_RAD= 0x04;
    public static final byte OBJ_ALARM_TEL= 0x05;
    public static final byte OBJ_WEIGHT_NET= 0x06;
    public static final byte OBJ_WEIGHT_GROSS= 0x07;
    public static final byte OBJ_POWER= 0x08;
    public static final byte OBJ_MILEAGE= 0x09;
    public static final byte OBJ_RSSI_1= 0x0A;
    public static final byte OBJ_RSSI_2= 0x0B;
    public static final byte OBJ_RSSI_3= 0x0C;

    private static final int MSG_BASE_SIZE = 3;

    private byte [] mBufMsgSend;
    private byte [] lastMsg = null;
    private OnQueryReplyListener mQueryReplyListener = null;
    private OnSetReplyListener mSetReplyListener = null;

    public ProtocolHelper()
    {
        mBufMsgSend = new byte[100];
        lastMsg = new byte[0];
    }

    public void setOnQueryReplyListener(OnQueryReplyListener l)
    {
        mQueryReplyListener = l;
    }

    public void setOnSetReplyListener(OnSetReplyListener l)
    {
        mSetReplyListener = l;
    }

    public byte[] querySpeedMsg()
    {
        return queryObjMsg(OBJ_SPEED);
    }

    public byte[] queryAngleMsg()
    {
        return queryObjMsg(OBJ_ANGLE);
    }

    public byte[] queryMaxSpeedMsg()
    {
        return queryObjMsg(OBJ_MAX_SPEED);
    }

    public byte[] queryAlarmRadMsg()
    {
        return queryObjMsg(OBJ_ALARM_RAD);
    }

    public byte[] queryAlarmTelMsg()
    {
        return queryObjMsg(OBJ_ALARM_TEL);
    }

    public byte[] queryWeightNetMsg()
    {
        return queryObjMsg(OBJ_WEIGHT_NET);
    }

    public byte[] queryWeightGrossMsg()
    {
        return queryObjMsg(OBJ_WEIGHT_GROSS);
    }

    public byte[] queryPowerMsg()
    {
        return queryObjMsg(OBJ_POWER);
    }

    public byte[] queryMileageMsg()
    {
        return queryObjMsg(OBJ_MILEAGE);
    }

    public byte[] queryAlarmRadAndTelMsg()
    {
        int iIdx = 0;
        mBufMsgSend[iIdx++] = OP_QUERY_REQ;
        mBufMsgSend[iIdx++] = 2;
        mBufMsgSend[iIdx++] = OBJ_ALARM_RAD;
        mBufMsgSend[iIdx++] = OBJ_ALARM_TEL;
        return construct0xFF(mBufMsgSend, iIdx);
    }

    public byte[] queryWeightNetAndGrossMsg()
    {
        int iIdx = 0;
        mBufMsgSend[iIdx++] = OP_QUERY_REQ;
        mBufMsgSend[iIdx++] = 2;
        mBufMsgSend[iIdx++] = OBJ_WEIGHT_NET;
        mBufMsgSend[iIdx++] = OBJ_WEIGHT_GROSS;
        return construct0xFF(mBufMsgSend, iIdx);
    }

    public byte[] setSpeedMsg(int iSpeed)
    {
        int iIdx = 0;
        mBufMsgSend[iIdx++] = OP_SET_REQ;
        mBufMsgSend[iIdx++] = 1;
        mBufMsgSend[iIdx++] = OBJ_SPEED;
        mBufMsgSend[iIdx++] = (byte)(iSpeed & 0xFF);
        return construct0xFF(mBufMsgSend, iIdx);
    }

    public byte[] setAngleMsg(int iAngle)
    {
        int iIdx = 0;
        mBufMsgSend[iIdx++] = OP_SET_REQ;
        mBufMsgSend[iIdx++] = 1;
        mBufMsgSend[iIdx++] = OBJ_ANGLE;
        mBufMsgSend[iIdx++] = (byte)(iAngle & 0xFF);
        mBufMsgSend[iIdx++] = (byte)((iAngle >> 8) & 0xFF);
        return construct0xFF(mBufMsgSend, iIdx);
    }

    public byte[] setSpeedAndAngleMsg(int iSpeed, int iAngle)
    {
        int iIdx = 0;
        mBufMsgSend[iIdx++] = OP_SET;
        mBufMsgSend[iIdx++] = 2;
        mBufMsgSend[iIdx++] = OBJ_SPEED;
        mBufMsgSend[iIdx++] = (byte)(iSpeed & 0xFF);
        mBufMsgSend[iIdx++] = OBJ_ANGLE;
        mBufMsgSend[iIdx++] = (byte)(iAngle & 0xFF);
        mBufMsgSend[iIdx++] = (byte)((iAngle >> 8) & 0xFF);
        return construct0xFF(mBufMsgSend, iIdx);
    }

    public byte[] setMaxSpeedMsg(int iMaxSpeed)
    {
        int iIdx = 0;
        mBufMsgSend[iIdx++] = OP_SET_REQ;
        mBufMsgSend[iIdx++] = 1;
        mBufMsgSend[iIdx++] = OBJ_MAX_SPEED;
        mBufMsgSend[iIdx++] = (byte)(iMaxSpeed & 0xFF);
        return construct0xFF(mBufMsgSend, iIdx);
    }

    public byte[] setAlarmRadMsg(int iAlarmRad)
    {
        int iIdx = 0;
        mBufMsgSend[iIdx++] = OP_SET_REQ;
        mBufMsgSend[iIdx++] = 1;
        mBufMsgSend[iIdx++] = OBJ_ALARM_RAD;
        mBufMsgSend[iIdx++] = (byte)(iAlarmRad & 0xFF);
        return construct0xFF(mBufMsgSend, iIdx);
    }

    public byte[] setAlarmTelMsg(String strAlarmTel)
    {
        int iIdx = 0;
        mBufMsgSend[iIdx++] = OP_SET_REQ;
        mBufMsgSend[iIdx++] = 1;
        mBufMsgSend[iIdx++] = OBJ_ALARM_TEL;
        byte [] tel = strAlarmTel.getBytes();
        System.arraycopy(tel, 0, mBufMsgSend, iIdx, 11);
        iIdx += 11;
        return construct0xFF(mBufMsgSend, iIdx);
    }

    public byte[] setAlarmRadAndTel(int iRad, String strTel)
    {
        int iIdx = 0;
        mBufMsgSend[iIdx++] = OP_SET_REQ;
        mBufMsgSend[iIdx++] = 2;
        mBufMsgSend[iIdx++] = OBJ_ALARM_RAD;
        mBufMsgSend[iIdx++] = (byte)(iRad & 0xFF);
        mBufMsgSend[iIdx++] = OBJ_ALARM_TEL;
        Arrays.fill(mBufMsgSend, iIdx, iIdx + 11, (byte)0x20);
        byte [] tel = strTel.getBytes();
        System.arraycopy(tel, 0, mBufMsgSend, iIdx, strTel.length() > 10 ? 11 : strTel.length());
        iIdx += 11;
        return construct0xFF(mBufMsgSend, iIdx);
    }

    public byte[] setWeightNetMsg(int iWeightNet)
    {
        int iIdx = 0;
        mBufMsgSend[iIdx++] = OP_SET_REQ;
        mBufMsgSend[iIdx++] = 1;
        mBufMsgSend[iIdx++] = OBJ_WEIGHT_NET;
        mBufMsgSend[iIdx++] = (byte)(iWeightNet & 0xFF);
        mBufMsgSend[iIdx++] = (byte)((iWeightNet >> 8) & 0xFF);
        return construct0xFF(mBufMsgSend, iIdx);
    }

    public byte[] setWeightGrossMsg(int iWeightGross)
    {
        int iIdx = 0;
        mBufMsgSend[iIdx++] = OP_SET_REQ;
        mBufMsgSend[iIdx++] = 1;
        mBufMsgSend[iIdx++] = OBJ_WEIGHT_GROSS;
        mBufMsgSend[iIdx++] = (byte)(iWeightGross & 0xFF);
        mBufMsgSend[iIdx++] = (byte)((iWeightGross >> 8) & 0xFF);
        return construct0xFF(mBufMsgSend, iIdx);
    }

    public byte[] setPowerMsg(int iPower)
    {
        int iIdx = 0;
        mBufMsgSend[iIdx++] = OP_SET_REQ;
        mBufMsgSend[iIdx++] = 1;
        mBufMsgSend[iIdx++] = OBJ_POWER;
        mBufMsgSend[iIdx++] = (byte)(iPower & 0xFF);
        return construct0xFF(mBufMsgSend, iIdx);
    }

    public byte[] setMileageMsg(int iMileage)
    {
        int iIdx = 0;
        mBufMsgSend[iIdx++] = OP_SET_REQ;
        mBufMsgSend[iIdx++] = 1;
        mBufMsgSend[iIdx++] = OBJ_MILEAGE;
        mBufMsgSend[iIdx++] = (byte)(iMileage & 0xFF);
        mBufMsgSend[iIdx++] = (byte)((iMileage >> 8) & 0xFF);
        mBufMsgSend[iIdx++] = (byte)((iMileage >> 16) & 0xFF);
        mBufMsgSend[iIdx++] = (byte)((iMileage >> 24) & 0xFF);
        return construct0xFF(mBufMsgSend, iIdx);
    }

    public byte [] setRssiMsg(int iRssi1, int iRssi2, int iRssi3)
    {
        int iIdx = 0;
        mBufMsgSend[iIdx++] = OP_SET;
        mBufMsgSend[iIdx++] = 3;
        mBufMsgSend[iIdx++] = OBJ_RSSI_1;
        mBufMsgSend[iIdx++] = (byte)(iRssi1 & 0xFF);
        mBufMsgSend[iIdx++] = OBJ_RSSI_2;
        mBufMsgSend[iIdx++] = (byte)(iRssi2 & 0xFF);
        mBufMsgSend[iIdx++] = OBJ_RSSI_3;
        mBufMsgSend[iIdx++] = (byte)(iRssi3 & 0xFF);
        return construct0xFF(mBufMsgSend, iIdx);
    }

    public void parseMsg(byte[] msgSrc)
    {
        byte[] msg = new byte[lastMsg.length + msgSrc.length];
        System.arraycopy(lastMsg, 0, msg, 0, lastMsg.length);
        System.arraycopy(msgSrc, 0, msg, lastMsg.length, msgSrc.length);

        int iStartIdx = 0;
        while (true)
        {
            int i;
            for (i = iStartIdx; i < msg.length - 1; i++)
            {
                if (msg[i] == FRAME_FLAG && msg[i + 1] != FRAME_FLAG)
                {
                    iStartIdx = i;
                    break;
                }
            }

            if (i < msg.length - 1)     // 找到开始标识
            {
                if (msg.length - iStartIdx >= MSG_BASE_SIZE)
                {
                    int tmpLength = (msg[iStartIdx + 1] & 0x7F) + ((msg[iStartIdx + 2] & 0x7F) << 7);
                    if (msg.length - iStartIdx >= tmpLength + MSG_BASE_SIZE)
                    {
                        // 处理
                        byte[] singleMsg = destruct0xFF(msg, iStartIdx, tmpLength + MSG_BASE_SIZE);
                        if (singleMsg == null)
                        {
                            lastMsg = new byte[0];
                            return;
                        }
                        handleObject(singleMsg);
                        iStartIdx +=  tmpLength + MSG_BASE_SIZE;
                        continue;
                    }
                }
                lastMsg = new byte[msg.length - iStartIdx];
                System.arraycopy(msg, iStartIdx, lastMsg, 0, msg.length - iStartIdx);
            }
            else
            {
                lastMsg = new byte[0];
            }
            break;
        }
    }

    @Contract(pure = true)
    private byte[] construct0xFF(byte[] msg, int iCount)
    {
        int iMsgLen = MSG_BASE_SIZE  + iCount;
        for (int i = 0; i < iCount; i++)
        {
            if (msg[i] == FRAME_FLAG) iMsgLen++;
        }

        byte[] dstMsg = new byte[iMsgLen];
        int j = MSG_BASE_SIZE;
        for (int i = 0; i < iCount; i++, j++)
        {
            if (msg[i] == (byte)FRAME_FLAG)
            {
                dstMsg[j++] = (byte)FRAME_FLAG;
            }
            dstMsg[j] = msg[i];
        }
        int iLen = dstMsg.length - MSG_BASE_SIZE;
        dstMsg[0] = FRAME_FLAG;
        dstMsg[1] = (byte)(iLen & 0x7F);
        dstMsg[2] = (byte)((iLen >> 7) & 0x7F);
        return dstMsg;
    }

    @Nullable
    @Contract(pure = true)
    private byte[] destruct0xFF(byte[] msg, int iStartIdx, int iLength)
    {
        int iMsgLen = iLength - MSG_BASE_SIZE;
        for (int i = iStartIdx + MSG_BASE_SIZE; i < iLength + iStartIdx - 1; i++)
        {
            if (msg[i] == FRAME_FLAG)
            {
                if (msg[i + 1] == FRAME_FLAG)
                {
                    iMsgLen--;
                    i++;
                }
                else
                {
                    return null;
                }
            }
        }

        byte[] dstMsg = new byte[iMsgLen];
        int j = 0;
        int i;
        for (i = iStartIdx + MSG_BASE_SIZE; i < iLength + iStartIdx - 1; i++, j++)
        {
            if (msg[i] == FRAME_FLAG && msg[i + 1] == FRAME_FLAG)
            {
                i++;
            }
            dstMsg[j] = msg[i];
        }
        if (i <= iLength + iStartIdx - 1)
        {
            dstMsg[j] = msg[i];
        }
        return dstMsg;
    }

    private int getObjValueSize(byte obj)
    {
        switch(obj)
        {
            case OBJ_SPEED: return 1;
            case OBJ_ANGLE: return 2;
            case OBJ_MAX_SPEED: return 1;
            case OBJ_ALARM_RAD: return 1;
            case OBJ_ALARM_TEL: return 11;
            case OBJ_WEIGHT_NET: return 2;
            case OBJ_WEIGHT_GROSS: return 2;
            case OBJ_POWER: return 1;
            case OBJ_MILEAGE: return 4;
        }
        return 0;
    }

    private byte[] queryObjMsg(byte obj)
    {
        mBufMsgSend[0] = OP_QUERY_REQ;
        mBufMsgSend[1] = 1;
        mBufMsgSend[2] = obj;
        return construct0xFF(mBufMsgSend, 3);
    }

    private void handleObject(byte[] msg)
    {
        byte op = msg[0];   // 0：op, 1: objnum
        byte obj;
        for (int i = 2; i < msg.length;)
        {
            obj = msg[i++];
            if (op== OP_QUERY_REPLY && null != mQueryReplyListener)
            {
                switch (obj)
                {
                    case OBJ_SPEED:     mQueryReplyListener.onSpeedGot(msg[i]);     break;
                    case OBJ_ANGLE:     mQueryReplyListener.onAngleGot(msg[i] | (msg[i + 1] << 8));     break;
                    case OBJ_MAX_SPEED:     mQueryReplyListener.onMaxSpeedGot(msg[i ]);     break;
                    case OBJ_ALARM_RAD:     mQueryReplyListener.onAlarmRadGot(msg[i] & 0x000000FF);      break;
                    case OBJ_ALARM_TEL:
                        byte [] tel = new byte[11];
                        System.arraycopy(msg, i, tel, 0, 11);
                        mQueryReplyListener.onAlarmTelGot(new String(tel)); break;
                    case OBJ_WEIGHT_NET:        mQueryReplyListener.onWeightNetGot(msg[i] | (msg[i + 1] << 8)); break;
                    case OBJ_WEIGHT_GROSS:      mQueryReplyListener.onWeightGrossGot(msg[i] | (msg[i + 1] << 8));   break;
                    case OBJ_POWER:     mQueryReplyListener.onPowerGot(msg[i]);     break;
                    case OBJ_MILEAGE:
                        int iMileage = msg[i] & 0xFF;
                        iMileage |= (msg[i + 1] << 8) & 0xFF00;
                        iMileage |= (msg[i + 2] << 16) & 0xFF0000;
                        iMileage |= (msg[i + 3] << 24) & 0xFF000000;
                        mQueryReplyListener.onMileageGot(iMileage);
                        break;
                }
                i += getObjValueSize(obj);
            }
            else if (op == OP_SET_REPLY && null != mSetReplyListener)
            {
                mSetReplyListener.onSetReply(getObjectName(obj));
            }
        }
    }

    private String getObjectName(int obj)
    {
        String strName = "";
        switch(obj)
        {
            case OBJ_SPEED:
                strName = "[运行速度]";
                break;
            case OBJ_ANGLE:
                strName = "[运行方位]";
                break;
            case OBJ_MAX_SPEED:
                strName = "[最大速度]";
                break;
            case OBJ_ALARM_RAD:
                strName = "[报警半径]";
                break;
            case OBJ_ALARM_TEL:
                strName = "[报警电话]";
                break;
            case OBJ_WEIGHT_NET:
                strName = "[净重]";
                break;
            case OBJ_WEIGHT_GROSS:
                strName = "[毛重]";
                break;
            case OBJ_POWER:
                strName = "[电量]";
                break;
            case OBJ_MILEAGE:
                strName = "[总里程]";
                break;
        }
        return strName;
    }

    private short getCrc(byte[] msg, int iStart, int iCount)
    {
        short wAccum = (short)0xffff;
        short wGenpoly = (short)0x8408;
        short temp = 0;
        for (int i = iStart; i < iCount + iStart; i++)
        {
            temp = msg[i];
            for (byte j = 8; j > 0; j--)
            {
                if (((temp ^ wAccum) & 0x0001) > 0)
                {
                    wAccum = (short)((wAccum >> 1) ^ wGenpoly);
                }
                else
                {
                    wAccum = (short)(wAccum >> 1);
                }
                temp = (short)(temp >> 1);
            }
        }
        return wAccum;

    }
}
