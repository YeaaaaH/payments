package payments.duo.service

import payments.duo.model.Category
import payments.duo.model.Payment
import payments.duo.model.auth.User
import payments.duo.model.request.CreatePaymentCommand
import payments.duo.model.response.PaymentResponse
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

    def "should return payments response monthly"() {
        given:
            List<Payment> payments = List.of(createValidPayment())
            PaymentResponse paymentResponse = createPaymentResponse()
        when:
            List<PaymentResponse> response = service.findAllByUserForYearAndMonth(1, 2021 , 9)
        then:
            1 * paymentRepository.findAllByUserForYearAndMonth(1, 2021, 9) >> payments
            !response.empty
            response.get(0).title == paymentResponse.title
            response.get(0).categoryName == paymentResponse.categoryName
            response.get(0).description == paymentResponse.description
            response.get(0).amount == paymentResponse.amount
    }

    def "should return payments response yearly"() {
        given:
            List<Payment> payments = List.of(createValidPayment(), createValidPayment())
            PaymentResponse paymentResponse = createPaymentResponse()
        when:
            List<PaymentResponse> response = service.findAllByUserForYear(1, 2021)
        then:
            1 * paymentRepository.findAllByUserForYear(1, 2021) >> payments
            response.size() == 2
            response.get(1).title == paymentResponse.title
            response.get(1).categoryName == paymentResponse.categoryName
            response.get(1).description == paymentResponse.description
            response.get(1).amount == paymentResponse.amount
    }

    private CreatePaymentCommand createValidCommand() {
        CreatePaymentCommand command = new CreatePaymentCommand()
        command.amount = 100
        command.categoryId = 2
        command.title = 'title'
        command.description = 'description'
        command.userId = 1
        command.createdOn = new Date(System.currentTimeMillis())
        command
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
        payment
    }

    private PaymentResponse createPaymentResponse() {
        PaymentResponse paymentResponse = new PaymentResponse(
                title: 'title',
                description: 'description',
                categoryName: 'category',
                amount: 100,
        )
        paymentResponse
    }
}