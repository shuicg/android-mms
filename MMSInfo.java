import android.content.Context;
import android.net.Uri;

import com.google.android.mms.InvalidHeaderValueException;
import com.google.android.mms.pdu.CharacterSets;
import com.google.android.mms.pdu.EncodedStringValue;
import com.google.android.mms.pdu.PduBody;
import com.google.android.mms.pdu.PduComposer;
import com.google.android.mms.pdu.PduHeaders;
import com.google.android.mms.pdu.PduPart;
import com.google.android.mms.pdu.SendReq;


public class MMSInfo {
        private Context con;
        private PduBody pduBody;
        private String recieverNum;
        private int partCount = 1;

        private String SUBJECT_STR = ""; //
        private boolean m_bHasText = false;
        private String m_sImageName = "";

        public MMSInfo(Context con, String recieverNum) {
                // TODO Auto-generated constructor stub
                this.con = con;
                this.recieverNum = recieverNum;
                pduBody = new PduBody();
        }


        public void addImagePart(String uriStr, String sName) {
                PduPart part = new PduPart();
                //part.setCharset(CharacterSets.UTF_8);
                m_sImageName = sName + ".jpg";
                part.setName(m_sImageName.getBytes());
                part.setContentId(sName.getBytes());
                part.setContentLocation(m_sImageName.getBytes());
                //part.setContentType((getTypeFromUri(uriStr)).getBytes());// "image/png"
                part.setContentType("image/jpeg".getBytes());
                part.setDataUri(Uri.parse(uriStr));
                pduBody.addPart(part);
        }

    public void addTextPart(String sText) {
        m_bHasText = true;
        PduPart part = new PduPart();
        part.setCharset(CharacterSets.UTF_8);
        part.setName(("text_0.txt").getBytes());
        part.setContentId(("text_0").getBytes());
        part.setContentLocation(("text_0.txt").getBytes());
        part.setContentType("text/plain".getBytes());
        part.setData(sText.getBytes());
        pduBody.addPart(part);
    }

        private String getTypeFromUri(String uriStr) {
                return uriStr.substring(uriStr.lastIndexOf("."), uriStr.length());
        }

        public byte[] getMMSBytes() {
                PduComposer composer = new PduComposer(con, initSendReq());
                return composer.make();
        }

        public void setSubject(String sTitle)
    {
        SUBJECT_STR = sTitle;
    }

        private void addSmilPart()
        {
            String sSmilXml = "<smil><head><layout><root-layout width=\"320px\" height=\"480px\"/>";

            if(m_bHasText)
            {
                sSmilXml += "<region id=\"Text\" left=\"0\" top=\"320\" width=\"320px\" height=\"160px\" fit=\"meet\"/>";
            }

            if(!m_sImageName.isEmpty())
            {
                sSmilXml += "<region id=\"Image\" left=\"0\" top=\"0\" width=\"320px\" height=\"320px\" fit=\"meet\"/>";
            }
            sSmilXml += "</layout></head><body><par dur=\"5000ms\">";

            if(m_bHasText)
            {
                sSmilXml += "<text src=\"text_0.txt\" region=\"Text\"/>";
            }
            if(!m_sImageName.isEmpty())
            {
                sSmilXml += "<img src=\""+ m_sImageName + "\" region=\"Image\"/>";
            }
            sSmilXml += "</par></body></smil>";

            PduPart part = new PduPart();
            part.setName(("smil.xml").getBytes());
            part.setContentId("smil".getBytes());
            part.setContentType("application/smil".getBytes());
            part.setContentLocation("smil.xml".getBytes());
            part.setData(sSmilXml.getBytes());
            pduBody.addPart(0, part);
        }

        private SendReq initSendReq() {
                SendReq req = new SendReq();
                EncodedStringValue[] sub = EncodedStringValue.extract(SUBJECT_STR);
                if (sub != null && sub.length > 0) {
                        req.setSubject(sub[0]);//
                }
                EncodedStringValue[] rec = EncodedStringValue.extract(recieverNum);
                if (rec != null && rec.length > 0) {
                        req.addTo(rec[0]);//
                }

                try {
                    req.setDeliveryReport(PduHeaders.VALUE_YES);
                    req.setDate(System.currentTimeMillis()/1000);
                    req.setExpiry(7*24*60*60);//7天过期
                    req.setPriority(PduHeaders.PRIORITY_NORMAL);
                    req.setReadReport(PduHeaders.VALUE_NO);
                    req.setMessageClass(PduHeaders.MESSAGE_CLASS_PERSONAL_STR.getBytes());

                } catch (InvalidHeaderValueException e) {
                    e.printStackTrace();
                }
                //new mms add smil
                addSmilPart();
                req.setBody(pduBody);
                return req;
        }

}