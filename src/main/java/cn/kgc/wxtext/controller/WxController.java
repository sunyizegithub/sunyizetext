package cn.kgc.wxtext.controller;

import cn.kgc.wxtext.config.WXConfig;
import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayConfig;
import com.github.wxpay.sdk.WXPayConstants;
import com.github.wxpay.sdk.WXPayUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
public class WxController {

   //8  25
    @Resource
    private WXConfig wxConfig;

    private WXPay wxPay;

    @Resource
    public WXPay setWXpay(WXPayConfig wxPayConfig){
        return this.wxPay=new WXPay(wxConfig);
    }
    //跳转到带有支付按钮的页面
    @RequestMapping(value = "/")
    public String pay(){
        return "";
    }


    //点击购买，生成二维码
    //提供我们的信息，通过微信方式，得到二维码连接
    @RequestMapping(value = "/createQcCode")
    public String createQcCode(HttpServletRequest request)throws  Exception{

        //这里可以放一些业务代码
        Map<String,String>data=new HashMap<>();
        //我的信息
        data.put("body","每时每刻给你新机会");
        data.put("total_fee","2");
        data.put("out_trade_no",getUUID());

        //生成二维码后 一直调用那个方法
        data.put("notify_url","12323");
        data.put("trade_type","NATIVE");
        data.put("product_id","1");

        //你的信息给它，他加工，给你一个map
        Map<String, String> stringStringMap = wxPay.unifiedOrder(data);
        if (stringStringMap.get("return_code").equals(WXPayConstants.SUCCESS)) {
            if (stringStringMap.get("result_code").equals(WXPayConstants.SUCCESS)){
                //这是二维码连接
                String code_url = stringStringMap.get("code_url");
                //把订单放入数据库 设为未支付    getUUID（）

                //把二维码地址放到页面上，使用工具生成
                request.setAttribute("code_url",code_url);
                return "pay";
            }else {
                request.setAttribute("error","code_url错误");
                return "error";
            }
        }else {
            request.setAttribute("error","code_url错误");
            return "error";
        }
    }

    @ResponseBody
    @RequestMapping(value = "/notifyAction")
    public String notifyAction (HttpServletRequest request) throws Exception{
        InputStream inputStream = request.getInputStream();
        BufferedReader bufferedInputStream = new BufferedReader(new InputStreamReader(inputStream));
       StringBuffer stringBuffer =new StringBuffer();
       while ((bufferedInputStream.readLine()!=null)){
           stringBuffer.append(bufferedInputStream);
       }
       //这是他给我们的支付结果
       Map<String, String> map = WXPayUtil.xmlToMap(stringBuffer+"");

        //验证回传是否靠谱   返回结果验证     是否返回成功
        boolean flag = wxPay.isPayResultNotifySignatureValid(map);
        if (flag){
            String returnCode = map.get("return_code");
            System.out.println("returnCode"+returnCode);
            if (map.get("return_code").equals(WXPayConstants.SUCCESS)){
                //是否支付成功
                //处理我们的业务
                if (returnCode.equals(WXPayConstants.SUCCESS)){
                    String tradeNo = map.get("out_trade_no");
                    //把支付状态修改为已提交
                    System.out.println("这是那一单"+tradeNo);
                    //组织数据告诉微信收到了
                    Map<String,String >answer=new HashMap<>();
                    answer.put("out_trade_no",tradeNo);
                    answer.put("return_msg","OK");
                    answer.put("return_code","SUCCESS");
                    return WXPayUtil.mapToXml(answer);
                }
            }
        }
        return "";
    }

    private String getUUID(){
        String rand=(int)Math.random()*100000+"";
        String format = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
        return format+rand;
    }
}
