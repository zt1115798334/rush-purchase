package org.example.rushpurchase.dto.mapper

import org.example.rushpurchase.mysql.entity.FileUploadInfo
import org.example.rushpurchase.dto.FileUploadInfoDto
import org.mapstruct.*

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
abstract class FileUploadInfoMapper {

    abstract fun toEntity(fileUploadInfoDto: FileUploadInfoDto): FileUploadInfo

    abstract fun toDto(fileUploadInfo: FileUploadInfo): FileUploadInfoDto

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    abstract fun partialUpdate(
        fileUploadInfoDto: FileUploadInfoDto,
        @MappingTarget fileUploadInfo: FileUploadInfo
    ): FileUploadInfo
}