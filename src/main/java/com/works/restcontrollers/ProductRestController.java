package com.works.restcontrollers;

import com.works.repositories._elastic.ProductElasticRepository;
import com.works.repositories._jpa.ProductRepository;
import com.works.repositories._redis.ProductSessionRepository;
import com.works.utils.REnum;
import com.works.utils.Util;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/rest/admin/product")
public class ProductRestController {

    final ProductRepository productRepository;
    final ProductSessionRepository productSessionRepository;
    final ProductElasticRepository productElasticRepository;

    public ProductRestController(ProductRepository productRepository, ProductSessionRepository productSessionRepository, ProductElasticRepository productElasticRepository) {
        this.productRepository = productRepository;
        this.productSessionRepository = productSessionRepository;
        this.productElasticRepository = productElasticRepository;
    }

    @DeleteMapping("/delete/{stIndex}")
    public Map<REnum, Object> productDelete(@RequestBody @PathVariable String stIndex) {
        Map<REnum, Object> hm = new LinkedHashMap<>();
        hm.put(REnum.MESSAGE, "Başarılı");
        productRepository.deleteById(Integer.valueOf(stIndex));
        productSessionRepository.deleteById(stIndex);
        productElasticRepository.deleteById(stIndex);
        hm.put(REnum.STATUS, true);
        return hm;
    }
    // *************************** Mvc-Pageable ***********************************
    //ELASTIC-DataTable
    @GetMapping("/datatable/list/{stSearchKey}")
    public Map<REnum, Object> productPageListSearch(HttpServletRequest request, @PathVariable String stSearchKey) {
        Map<String, String[]> allMap = request.getParameterMap();

        Map<REnum, Object> hm = new LinkedHashMap<>();
        hm.put(REnum.MESSAGE, "Başarılı");
        hm.put(REnum.STATUS, true);

        int validPage = Integer.parseInt(allMap.get("start")[0]) == 0 ? 0:(Integer.parseInt(allMap.get("start")[0]))/Integer.parseInt(allMap.get("length")[0]);
        hm.put(REnum.RESULT, (productElasticRepository.findByPr_name(stSearchKey +" "+ Util.theCompany.getCompany_name(), PageRequest.of(validPage, Integer.parseInt(allMap.get("length")[0])))).getContent());
        Integer totalCount = productElasticRepository.findByPr_name(stSearchKey +" "+ Util.theCompany.getCompany_name()).size();
        System.out.println("TOTAL -->" + totalCount);
        hm.put(REnum.ERROR, null);
        hm.put(REnum.COUNT, totalCount);
        hm.put(REnum.DRAW, Integer.parseInt(allMap.get("draw")[0]) );
        return hm;
    }

    //REDIS-DataTable
    @GetMapping("/datatable/list")
    public Map<REnum, Object> productPageList(HttpServletRequest request) {
        Map<String, String[]> allMap = request.getParameterMap();
        Map<REnum, Object> hm = new LinkedHashMap<>();
        hm.put(REnum.STATUS, true);
        hm.put(REnum.MESSAGE, "Başarılı");
        int validPage = Integer.parseInt(allMap.get("start")[0]) == 0 ? 0:(Integer.parseInt(allMap.get("start")[0]))/Integer.parseInt(allMap.get("length")[0]);

        hm.put(REnum.RESULT, productSessionRepository.findByOrderByIdAsc(PageRequest.of(validPage, Integer.parseInt(allMap.get("length")[0]))));
        //int filterCount = announcementSessionRepository.findByOrderByIdAsc(Util.theCompany.getCompany_name(),PageRequest.of(validPage, Integer.parseInt(allMap.get("length")[0]))).size();
        //hm.put(REnum.RESULT, announcementSessionRepository.findByOrderByIdAsc(Util.theCompany.getCompany_name(),PageRequest.of(validPage, Integer.parseInt(allMap.get("length")[0]))));
        hm.put(REnum.COUNT, productSessionRepository.count() );
        hm.put(REnum.DRAW, Integer.parseInt(allMap.get("draw")[0]) );
        return hm;
    }
}