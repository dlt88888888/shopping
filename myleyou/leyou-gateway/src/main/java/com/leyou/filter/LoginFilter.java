package com.leyou.filter;

import com.leyou.common.utils.CookieUtils;
import com.leyou.common.utils.JwtUtils;
import com.leyou.config.FilterProperties;
import com.leyou.config.JwtProperties;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
@EnableConfigurationProperties({JwtProperties.class, FilterProperties.class})
public class LoginFilter extends ZuulFilter {


    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private FilterProperties filterProperties;

    /**
     * pre：可以在请求被路由之前调用。
     * routing：在路由请求时候被调用。
     * post：在routing和error过滤器之后被调用。
     * error：处理请求时发生错误时被调用。
     * @return
     */
    @Override
    public String filterType() {

        return "pre";
    }

    /**
     * 数值越小优先级越高。
     * @return
     */
    @Override
    public int filterOrder() {
        return 5;
    }

    /**
     * 返回一个boolean类型来判断该过滤器是否要执行。我们可以通过此方法来指定过滤器的有效范围。
     * @return
     */
    @Override
    public boolean shouldFilter() {

        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();

        String requestURI = request.getRequestURI();
        //遍历允许访问的路径
        for (String path : this.filterProperties.getAllowPaths()) {
            if(requestURI.startsWith(path)){
                return false;
            }
        }

        return true;
    }

    @Override
    public Object run() throws ZuulException {
        //获取上下文
        RequestContext context = RequestContext.getCurrentContext();

        //获取request
        HttpServletRequest request = context.getRequest();
        //获取token对象
        String token = CookieUtils.getCookieValue(request, this.jwtProperties.getCookieName());

        //校验

        try {
            //校验通过什么都不做，放行
            JwtUtils.getInfoFromToken(token,this.jwtProperties.getPublicKey());
        } catch (Exception e) {
            //校验出现异常，返回403
            context.setSendZuulResponse(false);
            context.setResponseStatusCode(HttpStatus.SC_FORBIDDEN);
        }


        return null;
    }
}
