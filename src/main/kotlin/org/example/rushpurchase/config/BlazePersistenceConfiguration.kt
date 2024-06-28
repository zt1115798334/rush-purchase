package org.example.rushpurchase.config

import com.blazebit.persistence.Criteria
import com.blazebit.persistence.CriteriaBuilderFactory
import jakarta.persistence.EntityManagerFactory
import jakarta.persistence.PersistenceUnit
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.context.annotation.Scope

@Configuration
class BlazePersistenceConfiguration( @PersistenceUnit val entityManagerFactory: EntityManagerFactory) {
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    @Lazy(false)
    fun createCriteriaBuilderFactory(): CriteriaBuilderFactory {
        return Criteria.getDefault().createCriteriaBuilderFactory(entityManagerFactory)
    }
}