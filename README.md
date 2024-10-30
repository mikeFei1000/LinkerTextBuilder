![image](https://github.com/user-attachments/assets/4537d2e2-3c5b-49e3-9509-96777e17e03f)
# Usage
```java
//第一种文本超链接写法，如果是固定链接的可以直接填链接地址，不固定则可以填一个tag
String hrefContent = "我已阅读并同意<a href='event_one'>服务协议</a>、<a href='event_two'>隐私保护政策</a>和<a href='event_three'>第三方 SDK 共享信息情况说明</a>";
new Linker.Builder()
        .content(hrefContent)
        .bold(true) //是否加粗
        .linkColor(ContextCompat.getColor(getContext(), R.color.link_text_color)) //高亮的颜色
        .addOnLinkClickListener(content -> {
            if ("event_one".equals(content)) {
                ARApi.ready.goWebView("www.baidu.com").navigation();
            }
        })
        .textView(tvTest)
        .setLinkMovementMethod(LinkMovementMethod.getInstance())
        .apply();
```
```java
//第二种文本匹配法
String agree = "我已阅读并同意";
String serviceAgreement = "服务协议";
String privacyProtection = "隐私保护政策";
String sdkProtection = "第三方 SDK 共享信息情况说明";
new Linker.Builder()
        .content(agree + serviceAgreement + "、" + privacyProtection + "和" + sdkProtection)
        .bold(true)
        .links(serviceAgreement, privacyProtection, sdkProtection) //填入可以点击的文案列表
        .linkColor(ContextCompat.getColor(getContext(), R.color.link_text_color))
        .addOnLinkClickListener((content) -> {
            if (serviceAgreement.equals(content)) {
                ARApi.ready.goWebView("www.baidu.com").navigation();
            }
        })
        .textView(tvTest)
        .setLinkMovementMethod(LinkMovementMethod.getInstance())
        .apply();
```
