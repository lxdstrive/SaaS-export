package cn.itcast.controller.cargo;

import cn.itcast.controller.BaseController;
import cn.itcast.domain.cargo.ContractProduct;
import cn.itcast.domain.cargo.ContractProductExample;
import cn.itcast.domain.cargo.Factory;
import cn.itcast.domain.cargo.FactoryExample;
import cn.itcast.service.cargo.ContractProductService;
import cn.itcast.service.cargo.FactoryService;
import cn.itcast.utils.FileUploadUtil;
import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/cargo/contractProduct")
public class ContractProductController extends BaseController {

    @Reference
    private FactoryService factoryService;

    @Reference
    private ContractProductService contractProductService;

    @Autowired
    private FileUploadUtil fileUploadUtil;

    @RequestMapping(value = "/list",name = "显示购销合同货物列表数据")
    public String list(String contractId, @RequestParam(value = "page",defaultValue = "1") int page, @RequestParam(value = "pageSize",defaultValue = "5")int size){
        //查询生产厂家
        //select * from co_factory where ctype="货物"
        FactoryExample factoryExample = new FactoryExample();
        factoryExample.createCriteria().andCtypeEqualTo("货物");
        List<Factory> factoryList = factoryService.findAll(factoryExample);
        request.setAttribute("factoryList",factoryList);


        //查询当前合同下的货物
        //select * from co_contract_product where contract_id=?
        //查询货物下的附件(在映射文件中已经查询)
        ContractProductExample contractProductExample = new ContractProductExample();
        contractProductExample.createCriteria().andContractIdEqualTo(contractId);
        PageInfo pageInfo = contractProductService.findAll(contractProductExample, page, size);

        request.setAttribute("page",pageInfo);

        request.setAttribute("contractId",contractId);//保存货物时需要的合同id
        return "cargo/product/product-list";
    }

    @RequestMapping(value = "/edit",name = "合同下货物的保存")
    public String edit(ContractProduct contractProduct , MultipartFile productPhoto){
        String imagePath = null;
        try {
             imagePath = fileUploadUtil.upload(productPhoto);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            imagePath="";
        }

        contractProduct.setProductImage(imagePath);
        if (StringUtils.isEmpty(contractProduct.getId())){
            //新增
            contractProduct.setId(UUID.randomUUID().toString());
            contractProduct.setCompanyId(companyId);
            contractProduct.setCompanyName(companyName);
            contractProduct.setCreateBy(createBy);
            contractProduct.setCreateTime(new Date());
            contractProductService.save(contractProduct);
        }else {
            contractProduct.setUpdateBy(createBy);
            contractProduct.setUpdateTime(new Date());
            contractProductService.update(contractProduct);
            //编辑
        }

        return "redirect:/cargo/contractProduct/list.do?contractId="+contractProduct.getContractId();
    }

    @RequestMapping("/toUpdate")
    public String toUpdate(String id){

        ContractProduct contractProduct = contractProductService.findById(id);
        request.setAttribute("contractProduct",contractProduct);

        //查询生产厂家
        //select * from co_factory where ctype="货物"
        FactoryExample factoryExample = new FactoryExample();
        factoryExample.createCriteria().andCtypeEqualTo("货物");
        List<Factory> factoryList = factoryService.findAll(factoryExample);
        request.setAttribute("factoryList",factoryList);

        return "cargo/product/product-update";
    }

    @RequestMapping(value = "/delete",name = "删除货物信息")
    public String delete(String id,String contractId){
        contractProductService.delete(id);
        return "redirect:/cargo/contractProduct/list.do?contractId="+contractId;
    }

    @RequestMapping(value = "/toImport",name = "跳转到上传货物界面")
    public String toImport(String contractId){
        request.setAttribute("contractId",contractId);
        return "cargo/product/product-import";
    }

    @RequestMapping(value = "/import",name = "上传货物方法")
    public String importXls(String contractId,MultipartFile file) throws Exception{
        InputStream inputStream = file.getInputStream(); //从file获取输入流
        Workbook workbook = new XSSFWorkbook(inputStream); //创建了有内容的工作薄
        Sheet sheet = workbook.getSheetAt(0);  //获取工作表

        int lastRowIndex = sheet.getLastRowNum(); //获取当前sheet的最后一行的索引值
        ContractProduct contractProduct = null;
        Row row = null;
        List<ContractProduct> productList = new ArrayList<>();// 用来接收每个货物对象，为了能一次性调用service的方法
        for (int i = 1; i <= lastRowIndex; i++) {
            contractProduct = new ContractProduct();
            contractProduct.setId(UUID.randomUUID().toString());
            contractProduct.setCompanyId(companyId);
            contractProduct.setCompanyName(companyName);
            contractProduct.setCreateBy(createBy);
            contractProduct.setCreateTime(new Date());

            contractProduct.setContractId(contractId); //设置货物属于哪个合同

            row = sheet.getRow(i);//获取有效
//            生产厂家	货号	数量	包装单位(PCS/SETS)	装率	箱数	单价	货物描述	要求
            String factoryName = row.getCell(1).getStringCellValue(); //生产厂家
            contractProduct.setFactoryName(factoryName);
            String productNo = row.getCell(2).getStringCellValue();//货号
            contractProduct.setProductNo(productNo);
            Double cnumber = row.getCell(3).getNumericCellValue();//数量
            contractProduct.setCnumber(cnumber.intValue());
            String packingUnit = row.getCell(4).getStringCellValue();//包装单位(PCS/SETS)
            contractProduct.setPackingUnit(packingUnit);

            Double loadingRate = row.getCell(5).getNumericCellValue();//装率
            contractProduct.setLoadingRate(loadingRate.toString());

            Double boxNum = row.getCell(6).getNumericCellValue();//箱数
            contractProduct.setBoxNum(boxNum.intValue());

            Double price = row.getCell(7).getNumericCellValue();//单价
            contractProduct.setPrice(price);

            String productDesc = row.getCell(8).getStringCellValue();//货物描述
            contractProduct.setProductDesc(productDesc);

            String productRequest = row.getCell(9).getStringCellValue();//要求
            contractProduct.setProductRequest(productRequest);

            productList.add(contractProduct);
        }
        contractProductService.saveList(productList);

        return "redirect:/cargo/contractProduct/list.do?contractId="+contractId; //重定向到货物列表页面
    }


    //大标题的样式
    public CellStyle bigTitle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontName("宋体");
        font.setFontHeightInPoints((short) 16);
        font.setBold(true);//字体加粗
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER.CENTER);                //横向居中
        style.setVerticalAlignment(VerticalAlignment.CENTER);        //纵向居中
        return style;
    }

    //小标题的样式
    public CellStyle title(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontName("黑体");
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);                //横向居中
        style.setVerticalAlignment(VerticalAlignment.CENTER);        //纵向居中
        style.setBorderTop(BorderStyle.THIN);                        //上细线
        style.setBorderBottom(BorderStyle.THIN);                    //下细线
        style.setBorderLeft(BorderStyle.THIN);                        //左细线
        style.setBorderRight(BorderStyle.THIN);                        //右细线
        return style;
    }

    //文字样式
    public CellStyle text(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontName("Times New Roman");
        font.setFontHeightInPoints((short) 10);

        style.setFont(font);

        style.setAlignment(HorizontalAlignment.LEFT);                //横向居左
        style.setVerticalAlignment(VerticalAlignment.CENTER);        //纵向居中
        style.setBorderTop(BorderStyle.THIN);                        //上细线
        style.setBorderBottom(BorderStyle.THIN);                    //下细线
        style.setBorderLeft(BorderStyle.THIN);                        //左细线
        style.setBorderRight(BorderStyle.THIN);                        //右细线

        return style;
    }


}
