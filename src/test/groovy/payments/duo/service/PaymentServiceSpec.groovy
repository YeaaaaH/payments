package payments.duo.service

import payments.duo.model.Category
import payments.duo.model.Payment
import payments.duo.model.auth.User
import payments.duo.model.request.CreatePaymentCommand
import payments.duo.repository.CategoryRepository
import payments.duo.repository.PaymentRepository
import spock.lang.Specification

import java.sql.Date

class PaymentServiceSpec extends Specification {

    UserService userService
    CategoryRepository categoryRepository
    PaymentRepository paymentRepository
    PaymentService service = new PaymentService(userService, paymentRepository, categoryRepository)

    def setup() {
        userService = Mock(UserService)
        categoryRepository = Mock(CategoryRepository)
        paymentRepository = Mock(PaymentRepository)
        service = new PaymentService(userService, paymentRepository, categoryRepository)
    }

    def "should save valid user"() {
        given:
            CreatePaymentCommand command = createValidCommand()
        and:
            Optional<Category> optCategory = Optional.of(new Category(name: 'category'))
            User user = new User(id: 1, username: 'username')
        when:
            service.savePayment(command)
        then:
            1 * categoryRepository.findById(_) >> optCategory
            1 * userService.findUserById(_) >> user
            1 * paymentRepository.save(_)
    }

    def "should payment by id"() {
        given:
            Payment payment = createValidPayment()
            Optional<Payment> optPayment = Optional.of(payment)
        when:
            service.findPaymentById(1)
        then:
            1 * paymentRepository.findById(1) >> optPayment
    }

    private CreatePaymentCommand createValidCommand() {
        CreatePaymentCommand command = new CreatePaymentCommand()
        command.amount = 100
        command.categoryId = 2
        command.title = 'title'
        command.description = 'description'
        command.userId = 1
        command.createdOn = new Date(System.currentTimeMillis())
        return command
    }

    private Payment createValidPayment() {
        Payment payment = new Payment()
        payment.amount = 100
        Category category = new Category(
                name: 'category'
        )
        payment.category = category
        payment.title = 'title'
        payment.description = 'description'
        payment.createdOn = new Date(System.currentTimeMillis())
        User user = new User(
                id: 1,
                username: 'username'
        )
        payment.user = user
        return payment
    }
}