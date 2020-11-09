package com.dragonflyxd.dfcb.components.common.service.impl;

import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dragonflyxd.dfcb.components.common.dao.entity.BaseEntity;
import com.dragonflyxd.dfcb.components.common.dao.mapper.BaseMapper;
import com.dragonflyxd.dfcb.components.common.service.BaseService;
import com.dragonflyxd.dfcb.components.common.web.dto.BaseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

/**
 * Service - 基类实现类
 *
 * @author longfei.chen
 * @since 2020.10.28
 **/
@Slf4j
public class BaseServiceImpl<E extends BaseEntity, D extends BaseDTO, M extends BaseMapper<E>> extends ServiceImpl<M, E> implements BaseService<E, D> {
    protected Class<D> dtoClass = currentDTOClass();

    /**
     * DTO转换成实体类
     *
     * @param dto DTO
     * @return 实体类
     */
    @Override
    public E dtoToEntity(D dto) {
        E entity;

        try {
            entity = (E) entityClass.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            log.error("BaseServiceImpl.dtoToEntity err：{}", ex.getMessage());

            return null;
        }

        BeanUtils.copyProperties(dto, entity);

        return entity;
    }

    /**
     * 实体类转换成DTO
     *
     * @param entity 实体类
     * @return DTO
     */
    @Override
    public D entityToDTO(E entity) {
        D dto;

        try {
            dto = dtoClass.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            log.error("BaseServiceImpl.entityToDTO err：{}", ex.getMessage());

            return null;
        }

        BeanUtils.copyProperties(entity, dto);

        return dto;
    }

    /**
     * 获取DTO类型
     *
     * @return DTO类型
     */
    protected Class<D> currentDTOClass() {
        return (Class<D>) ReflectionKit.getSuperClassGenericType(getClass(), 1);
    }
}