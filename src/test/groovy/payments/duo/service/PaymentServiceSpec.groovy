package payments.duo.service

import payments.duo.model.Category
import payments.duo.model.Payment
import payments.duo.model.auth.User
import payments.duo.model.request.CreatePaymentCommand
import payments.duo.model.request.CreatePaymentsListCommand
import payments.duo.model.response.PaymentReportResponse
import payments.duo.model.response.PaymentReportResponseParameters
import payments.duo.model.response.PaymentResponse
import payments.duo.repository.PaymentRepository
import spock.lang.Specification

import java.sql.Date

import static payments.duo.service.PaymentService.getPaymentReportResponse

class PaymentServiceSpec extends Specification {

    UserService userService
    CategoryService categoryService
    PaymentRepository paymentRepository
    PaymentService service = new PaymentService(userService, paymentRepository, categoryService)

    def setup() {
        userService = Mock(UserService)
        categoryService = Mock(CategoryService)
        paymentRepository = Mock(PaymentRepository)
        service = new PaymentService(userService, paymentRepository, categoryService)
    }

    def "should save valid payment"() {
        given:
            CreatePaymentCommand command = createValidCommand()
            Payment payment = createValidPayment()
        and:
            Category optCategory = new Category(name: 'category')
            User user = new User(id: 1, username: 'username')
        when:
            service.savePayment(command)
        then:
            1 * categoryService.findCategoryById(_) >> optCategory
            1 * userService.findUserById(_) >> user
            1 * paymentRepository.save(_) >> payment
    }

    def "should save batch of payments"() {
        given:
            CreatePaymentCommand command1 = createValidCommand()
            CreatePaymentCommand command2 = createValidCommand()
            CreatePaymentsListCommand commandsList = new CreatePaymentsListCommand(
                    paymentCommands: new ArrayList<CreatePaymentCommand>([command1, command2])
            )
        and:
            Category optCategory = new Category(name: 'category')
            User user = new User(id: 1, username: 'username')
        when:
            service.saveAllPayments(commandsList.paymentCommands)
        then:
            2 * categoryService.findCategoryById(_) >> optCategory
            2 * userService.findUserById(_) >> user
            1 * paymentRepository.saveAll(_ as List<Payment>)
    }

    def "should find payment by id"() {
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

    def "should return payments report response yearly"() {
        given:
            List<PaymentReportResponseParameters> params = List.of(
                    createPaymentReportParams("Food", 100 as BigDecimal),
                    createPaymentReportParams("Cloth", 200 as BigDecimal)
            )
        when:
            PaymentReportResponse response = service.calculateYearlyByUserAndCategory(1, 2021)
        then:
            1 * paymentRepository.calculateYearlyByUserAndCategory(1, 2021) >> params
            response.report.size() == 2
            response.report.get("Food") == 100
            response.report.get("Cloth") == 200

    }

    def "should return payments report response monthly"() {
        given:
            List<PaymentReportResponseParameters> params = List.of(
                    createPaymentReportParams("Food", 100 as BigDecimal),
                    createPaymentReportParams("Cloth", 200 as BigDecimal)
            )
            PaymentReportResponse reportResponse = getPaymentReportResponse(params)
        when:
            PaymentReportResponse response = service.calculateMonthlyByUserAndCategory(1, 2021 , 9)
        then:
            1 * paymentRepository.calculateMonthlyByUserAndCategory(1, 2021, 9) >> params
            response.report.size() == 2
            response.report.get("Food") == 100
            response.report.get("Cloth") == 200
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

    private PaymentReportResponseParameters createPaymentReportParams(String category, BigDecimal amount) {
        PaymentReportResponseParameters parameters = new PaymentReportResponseParameters(
                category: category,
                amount: amount
        )
        parameters
    }
}