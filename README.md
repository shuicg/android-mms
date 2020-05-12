# androidmms
彩信发送代码，加smil的版本

用法：
    String sPhone = "13066666666";  
    final MMSInfo mms = new MMSInfo(MMSTest1Activity.this, sPhone);//  
    String path = "file:///data/data/aaa.com.mmstest/1.png";//  
    mms.addImagePart(path, "image");  
    mms.addTextPart("内容");  
    mms.setSubject("主题");  
    //mmsc http://mmsc.myuni.com.cn  
    NetUtil.PostSendMms(MMSTest1Activity.this,"http://mmsc.myuni.com.cn", mms.getMMSBytes());//  

