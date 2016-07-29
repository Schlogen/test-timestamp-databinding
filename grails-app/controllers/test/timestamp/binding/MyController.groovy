package test.timestamp.binding

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class MyController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond My.list(params), model:[myCount: My.count()]
    }

    def show(My my) {
        respond my
    }

    def create() {
        respond new My(params)
    }

    @Transactional
    def save(My my) {
        if (my == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (my.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond my.errors, view:'create'
            return
        }

        my.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'my.label', default: 'My'), my.id])
                redirect my
            }
            '*' { respond my, [status: CREATED] }
        }
    }

    def edit(My my) {
        respond my
    }

    @Transactional
    def update(My my) {
        if (my == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (my.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond my.errors, view:'edit'
            return
        }

        my.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'my.label', default: 'My'), my.id])
                redirect my
            }
            '*'{ respond my, [status: OK] }
        }
    }

    @Transactional
    def delete(My my) {

        if (my == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        my.delete flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'my.label', default: 'My'), my.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'my.label', default: 'My'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
