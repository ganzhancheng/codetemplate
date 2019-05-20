package com.bgyfw.erp.resources.honor.util;

import com.bgyfw.erp.resources.honor.entity.TbDictionaryHonornumber;
import com.bgyfw.erp.resources.honor.entity.TbHonor;
import com.bgyfw.erp.resources.honor.entity.TbHonorDTO;
import com.bgyfw.erp.resources.honor.entity.TbHsprCommunity;
import com.bgyfw.erp.resources.honor.entity.TbSysOrgan;
import com.bgyfw.erp.resources.honor.repository.TbDictionaryHonornumberMapper;
import com.bgyfw.erp.resources.honor.repository.TbHonorMapper;
import com.bgyfw.erp.resources.honor.repository.TbHsprCommunityMapper;
import com.bgyfw.erp.resources.honor.repository.TbSysOrganMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ValidateUtil implements IValidator<TbHonorDTO> {
    List<TbSysOrgan> organs;
    List<TbHsprCommunity> communities;
    List<TbDictionaryHonornumber> dictionaryHonornumbers;
    List<TbHonor> tbHonors;
    @Autowired
    TbSysOrganMapper tbSysOrganMapper;
    @Autowired
    TbHsprCommunityMapper tbHsprCommunityMapper;
    @Autowired
    TbDictionaryHonornumberMapper tbDictionaryHonornumberMapper;
    @Autowired
    TbHonorMapper tbHonorMapper;


    @Override
    public void beforeValidate() {
        TbHsprCommunity community = new TbHsprCommunity();
        community.setIsdelete((short) 0);
        communities = tbHsprCommunityMapper.select(community);

        TbSysOrgan tbSysOrgan = new TbSysOrgan();
        tbSysOrgan.setIsdelete((short) 0);
        organs = tbSysOrganMapper.select(tbSysOrgan);
        dictionaryHonornumbers = tbDictionaryHonornumberMapper.select(null);
        tbHonors = tbHonorMapper.select(null);

    }

    public boolean validateOrgan(TbHonorDTO tbHonorDTO){

        List<TbSysOrgan> list = organs.stream().filter(o -> o.getOrganname().equals(tbHonorDTO.getOrganname())).collect(Collectors.toList());

        if (list.size() > 0) {
            TbSysOrgan tbSysOrgan = list.get(0);
            tbHonorDTO.setOrgancode(tbSysOrgan.getOrgancode());
            return true;
        }else {
            String s = setResult(tbHonorDTO.getResult(), "区域填写错误");
            tbHonorDTO.setResult(s);
            return false;
        }
    }

    public boolean validateComm(TbHonorDTO tbHonorDTO){
        List<TbHsprCommunity> list = communities.stream().filter(o -> o.getCommname().equals(tbHonorDTO.getCommname())).collect(Collectors.toList());

        if (list.size() > 0) {
            TbHsprCommunity community = list.get(0);
            tbHonorDTO.setCommid(community.getCommid());
            return true;
        }else {
            String s = setResult(tbHonorDTO.getResult(), "项目填写错误");
            tbHonorDTO.setResult(s);
            return false;
        }
    }

    /**
     * 验证所有字段非空
     * @param tbHonorDTO
     * @return
     */
    public boolean validateEmpty(TbHonorDTO tbHonorDTO){

        boolean f = true;
        Class<? extends TbHonorDTO> aClass = tbHonorDTO.getClass();

        Field[] declaredFields = aClass.getDeclaredFields();
        for (Field field : declaredFields) {
            ExcelField excelField = field.getAnnotation(ExcelField.class);
            if (excelField != null) {
                field.setAccessible(true);
                try {
                    NotNull notnull = field.getAnnotation(NotNull.class);

                    NotEmpty notempty = field.getAnnotation(NotEmpty.class);

                    if (notnull != null || notempty != null) {
                        Object value = field.get(tbHonorDTO);
                        Class<?> type = field.getType();
                        if (type == String.class) {
                            String str = (String) value;
                            if (StringUtils.isBlank(str)) {
                                String s = setResult(tbHonorDTO.getResult(), excelField.name()+"填写错误");
                                tbHonorDTO.setResult(s);
                                f = false;
                            }
                        }else{
                            if (value == null) {
                                String s = setResult(tbHonorDTO.getResult(), excelField.name()+"填写错误");
                                tbHonorDTO.setResult(s);
                                f = false;
                            }
                        }

                    }

                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

        }
        return f;
    }

    public boolean honorname(TbHonorDTO tbHonorDTO,List<TbHonorDTO> success){
        boolean b = success.stream().anyMatch(dto -> dto.getHonorname().equals(tbHonorDTO.getHonorname()));
        if (b) {
            String s = setResult(tbHonorDTO.getResult(), "荣誉名字已存在相同数据");
            tbHonorDTO.setResult(s);
            return false;
        }

        boolean b1 = tbHonors.stream().anyMatch(tbHonor -> tbHonor.getHonorname().equals(tbHonorDTO.getHonorname()));

        if (b1) {
            String s = setResult(tbHonorDTO.getResult(), "荣誉名字已存在相同数据");
            tbHonorDTO.setResult(s);
            return false;
        }

        return true;
    }
    public boolean honorstatus(TbHonorDTO tbHonorDTO){
        String honorstatus = tbHonorDTO.getHonorstatus();
        if ("在册".equals(honorstatus) || "遗失".equals(honorstatus)) {
            return true;
        }else{
            String s = setResult(tbHonorDTO.getResult(), "荣誉状态请填写列表中的数值");
            tbHonorDTO.setResult(s);
            return false;
        }
    }

    public boolean longtermeffective(TbHonorDTO tbHonorDTO){
        String longtermeffective = tbHonorDTO.getLongtermeffective();
        if ("是".equals(longtermeffective)) {
            if (tbHonorDTO.getEffectivetime() != null) {
                String s = setResult(tbHonorDTO.getResult(), "长期有效的荣誉不需要填写有效期");
                tbHonorDTO.setResult(s);
                return false;
            }else{
                return true;
            }
        }else{
            if (tbHonorDTO.getEffectivetime() == null) {
                String s = setResult(tbHonorDTO.getResult(), "请填写有效期");
                tbHonorDTO.setResult(s);
                return false;
            }else{
                return true;
            }
        }
    }

    public boolean honorlevel(TbHonorDTO tbHonorDTO){
        String honorlevel = tbHonorDTO.getHonorlevel();
        if ("国家级".equals(honorlevel) || "省级".equals(honorlevel)|| "市级".equals(honorlevel)|| "其他".equals(honorlevel)) {
            return true;
        }else{
            String s = setResult(tbHonorDTO.getResult(), "荣誉级别请填写列表中的数值");
            tbHonorDTO.setResult(s);
            return false;
        }
    }

    public String setResult(String result, String info) {
        if (StringUtils.isNotBlank(result)) {
            result = result +","+ info;
        }else{
            result = info;
        }
        return result;
    }

    @Override
    public void afterValidate(List<TbHonorDTO> success,List<TbHonorDTO> error) {
        for (TbHonorDTO tbHonorDTO : success) {
            String organcode = tbHonorDTO.getOrgancode();
            String erpHonorNumber = getErpHonorNumber(organcode);
            tbHonorDTO.setErphonornumber(erpHonorNumber);
        }

        for (TbDictionaryHonornumber dictionaryHonornumber : dictionaryHonornumbers) {
            tbDictionaryHonornumberMapper.updateNumber(dictionaryHonornumber);
        }
    }

    public String getErpHonorNumber(String organcode) {

        List<TbDictionaryHonornumber> list = dictionaryHonornumbers.stream().filter(d -> d.getOrgancode().equals(organcode)).collect(Collectors.toList());

        if (list.size()== 0){
            list = dictionaryHonornumbers;
        }
        TbDictionaryHonornumber number = list.get(0);
        String serialNumber = number.getSerialnumber() + "";
        StringBuilder sb = new StringBuilder();
        sb.append("RY");
        sb.append(number.getOrgannum());
        for (int i = 0; i < 6 - serialNumber.length(); i++) {
            sb.append("0");
        }
        sb.append(serialNumber);

        number.setSerialnumber(number.getSerialnumber()+1);
        return sb.toString();
    }
}
