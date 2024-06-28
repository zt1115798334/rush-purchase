package org.example.rushpurchase.shiro.factory

import org.apache.shiro.subject.Subject
import org.apache.shiro.subject.SubjectContext
import org.apache.shiro.web.mgt.DefaultWebSubjectFactory

class StatelessWebSubjectFactory : DefaultWebSubjectFactory() {
    override fun createSubject(context: SubjectContext?): Subject {
        context?.let {
            context.isSessionCreationEnabled = false
        }
        return super.createSubject(context)
    }
}