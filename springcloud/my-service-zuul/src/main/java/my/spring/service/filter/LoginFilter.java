package my.spring.service.filter;


import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class LoginFilter extends ZuulFilter {
    @Override
    public String filterType() {
        //返回字符串  ，代表过滤类型  包含以下4种
        //pre 请求路由之前执行
        //route 在路由请求时调用
        //post  在route和error过滤器之后调用
        //error  处理请求时发生错误调用
        return "pre";
    }

    @Override
    public int filterOrder() {
        //返回int类型的值  值越小 过滤器的执行顺序优先级越高
        return 5;
    }

    @Override
    public boolean shouldFilter() {
        // 判断该过滤器是否需要执行    true 执行  ;false  不执行
        return true;
    }

    @Override
    public Object run() throws ZuulException {

        //获取zuul网关提供的上下文对象
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();
        String token = request.getParameter("access-token");
        if(StringUtils.isBlank(token)){
            context.setSendZuulResponse(false);

            context.setResponseStatusCode(HttpStatus.SC_UNAUTHORIZED);
            //设置响应信息
            context.setResponseBody("{\\\"status\\\":\\\"401\\\", \\\"text\\\":\\\"request error!\\\"}");
            context.set("token",token);
        }


        return null;
    }
}
