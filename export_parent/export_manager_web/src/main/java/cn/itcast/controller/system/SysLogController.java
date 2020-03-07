package cn.itcast.controller.system;

import cn.itcast.controller.BaseController;
import cn.itcast.service.aspect.SysLogService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/system/log")
public class SysLogController extends BaseController {

    @Autowired
    private SysLogService sysLogService;
    /**
     * 返回全部列表
     */
    @RequestMapping(value="/list",name="系统日志列表")
    public String  list(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size){
        PageInfo result = sysLogService.findPage(companyId,page, size);
        request.setAttribute("page",result);
        return "/system/log/log-list";
    }
}
