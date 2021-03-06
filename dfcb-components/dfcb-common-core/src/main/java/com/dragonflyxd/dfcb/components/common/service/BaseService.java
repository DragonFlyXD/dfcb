package com.dragonflyxd.dfcb.components.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dragonflyxd.dfcb.components.common.dao.entity.BaseEntity;
import com.dragonflyxd.dfcb.components.context.dto.BaseDTO;
import com.dragonflyxd.dfcb.components.context.emuns.DeleteFlagEnum;
import com.dragonflyxd.dfcb.components.context.emuns.ResponseCodeEnum;
import com.dragonflyxd.dfcb.components.context.util.AssertUtil;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service - 基类
 *
 * @author longfei.chen
 * @since 2020.10.27
 **/
public interface BaseService<E extends BaseEntity, D extends BaseDTO> extends IService<E> {
    /**
     * 查询所有
     *
     * @return DTO集合
     */
    default List<D> findAll() {
        return entitiesToDTOs(list());
    }

    /**
     * 根据主键查询
     *
     * @param id 主键
     * @return DTO
     */
    default D findById(Long id) {
        AssertUtil.notNull(id, ResponseCodeEnum.PARAMS_INVALID.getCode());

        return entityToDTO(getById(id));
    }

    /**
     * 根据主键集合查询
     *
     * @param ids 主键集合
     * @return DTO集合
     */
    default List<D> findByIds(List<Long> ids) {
        AssertUtil.notEmpty(ids, ResponseCodeEnum.PARAMS_INVALID.getCode());

        return entitiesToDTOs(listByIds(ids));
    }

    /**
     * 保存
     *
     * @param dto DTO
     * @return DTO
     */
    @Transactional(rollbackFor = Exception.class)
    default D save(D dto) {
        AssertUtil.notNull(dto, ResponseCodeEnum.PARAMS_INVALID.getCode());
        AssertUtil.isTrue(save(dtoToEntity(dto, false)), ResponseCodeEnum.SAVE_FAILED.getCode());

        return dto;
    }

    /**
     * 批量保存
     *
     * @param dtos DTO集合
     * @return DTO集合
     */
    @Transactional(rollbackFor = Exception.class)
    default List<D> saveBatch(List<D> dtos) {
        AssertUtil.notEmpty(dtos, ResponseCodeEnum.PARAMS_INVALID.getCode());
        AssertUtil.isTrue(saveBatch(dtosToEntities(dtos, false), dtos.size()), ResponseCodeEnum.SAVE_BATCH_FAILED.getCode());

        return dtos;
    }

    /**
     * 更新
     *
     * @param dto DTO
     * @return DTO
     */
    @Transactional(rollbackFor = Exception.class)
    default D update(D dto) {
        AssertUtil.notNull(dto, ResponseCodeEnum.PARAMS_INVALID.getCode());
        AssertUtil.isTrue(updateById(dtoToEntity(dto, true)), ResponseCodeEnum.UPDATE_FAILED.getCode());

        return dto;
    }

    /**
     * 检查并更新
     *
     * @param id  主键
     * @param dto DTO
     * @return DTO
     */
    @Transactional(rollbackFor = Exception.class)
    default D checkAndUpdate(Long id, D dto) {
        AssertUtil.notNull(id, ResponseCodeEnum.PARAMS_INVALID.getCode());

        AssertUtil.notNull(getById(id), ResponseCodeEnum.QUERY_FAILED.getCode());

        update(dto);

        return dto;
    }

    /**
     * 批量更新
     *
     * @param dtos DTO集合
     * @return DTO集合
     */
    @Transactional(rollbackFor = Exception.class)
    default List<D> updateBatch(List<D> dtos) {
        AssertUtil.notEmpty(dtos, ResponseCodeEnum.PARAMS_INVALID.getCode());
        AssertUtil.isTrue(updateBatchById(dtosToEntities(dtos, true), dtos.size()), ResponseCodeEnum.UPDATE_BATCH_FAILED.getCode());

        return dtos;
    }

    /**
     * 删除
     *
     * @param dto DTO
     * @return DTO
     */
    @Transactional(rollbackFor = Exception.class)
    default D delete(D dto) {
        AssertUtil.notNull(dto, ResponseCodeEnum.PARAMS_INVALID.getCode());

        dto.setDeleteFlag(DeleteFlagEnum.DELETED.getCode());
        AssertUtil.isTrue(updateById(dtoToEntity(dto, true)), ResponseCodeEnum.DELETE_FAILED.getCode());

        return dto;
    }

    /**
     * 检查并删除
     *
     * @param id  主键
     * @param dto DTO
     * @return DTO
     */
    @Transactional(rollbackFor = Exception.class)
    default D checkAndDelete(Long id, D dto) {
        AssertUtil.notNull(id, ResponseCodeEnum.PARAMS_INVALID.getCode());

        AssertUtil.notNull(getById(id), ResponseCodeEnum.QUERY_FAILED.getCode());

        delete(dto);

        return dto;
    }

    /**
     * 批量删除
     *
     * @param dtos DTO集合
     * @return DTO集合
     */
    @Transactional(rollbackFor = Exception.class)
    default List<D> deleteBatch(List<D> dtos) {
        AssertUtil.notEmpty(dtos, ResponseCodeEnum.PARAMS_INVALID.getCode());

        dtos.forEach(v -> v.setDeleteFlag(DeleteFlagEnum.DELETED.getCode()));
        AssertUtil.isTrue(updateBatchById(dtosToEntities(dtos, true), dtos.size()), ResponseCodeEnum.DELETE_BATCH_FAILED.getCode());

        return dtos;
    }

    /**
     * 设置保存的共通字段
     *
     * @param dto DTO
     */
    void setSaveColumns(D dto);

    /**
     * 设置更新的共通字段
     *
     * @param dto DTO
     */
    void setUpdateColumns(D dto);

    /**
     * DTO转换成实体类
     *
     * @param dto DTO
     * @return 实体类
     */
    E dtoToEntity(D dto);

    /**
     * DTO转换成实体类, 并设置共通字段
     *
     * @param dto DTO
     * @return 实体类
     */
    default E dtoToEntity(D dto, boolean isUpdate) {
        if (isUpdate) {
            setUpdateColumns(dto);
        } else {
            setSaveColumns(dto);
        }

        return dtoToEntity(dto);
    }

    /**
     * DTO批量转换成实体类
     *
     * @param dtos DTO集合
     * @return 实体类集合
     */
    default List<E> dtosToEntities(List<D> dtos) {
        return dtos.stream().map(this::dtoToEntity).collect(Collectors.toList());
    }

    /**
     * DTO批量转换成实体类
     *
     * @param dtos DTO集合
     * @return 实体类集合
     */
    default List<E> dtosToEntities(List<D> dtos, boolean isUpdate) {
        return dtos.stream().map(v -> dtoToEntity(v, isUpdate)).collect(Collectors.toList());
    }

    /**
     * 实体类转换成DTO
     *
     * @param entity 实体类
     * @return DTO
     */
    D entityToDTO(E entity);

    /**
     * 实体类批量转换成DTO
     *
     * @param entities 实体类集合
     * @return DTO集合
     */
    default List<D> entitiesToDTOs(List<E> entities) {
        return entities.stream().map(this::entityToDTO).collect(Collectors.toList());
    }
}