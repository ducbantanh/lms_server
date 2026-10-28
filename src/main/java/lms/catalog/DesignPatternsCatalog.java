package lms.catalog;

import java.util.*;

/**
 * Danh mục toàn diện các Mẫu thiết kế phần mềm (Design Patterns).
 * Bao gồm đầy đủ 23 mẫu GoF (Creational, Structural, Behavioral)
 * kết hợp các Kiến trúc xí nghiệp nâng cao (Enterprise Architecture Patterns).
 */
public final class DesignPatternsCatalog {

    public record DesignPatternItem(
            String id,
            String name,
            String vietnameseName,
            String category,
            String intent,
            String motivation,
            String diagramAscii,
            String codeExample,
            List<String> pros,
            List<String> cons,
            List<String> springBootUsage
    ) {}

    private static final Map<String, DesignPatternItem> PATTERNS = new LinkedHashMap<>();

    static {
        registerPattern(new DesignPatternItem(
                "PAT-01",
                "Singleton Pattern",
                "Mẫu đơn thể",
                "CREATIONAL",
                "Đảm bảo một lớp chỉ có duy nhất một thực thể (instance) và cung cấp một điểm truy cập toàn cục tới nó.",
                "Cần quản lý tài nguyên dùng chung như Connection Pool, Logger, Cấu hình hệ thống tập trung.",
                "+----------------------+\n|      Singleton       |\n+----------------------+\n| - instance: Singleton|\n+----------------------+\n| + getInstance(): S   |\n+----------------------+",
                "public final class DatabaseConnectionPool {\n    private static volatile DatabaseConnectionPool instance;\n    private final List<Object> pool = new ArrayList<>();\n\n    private DatabaseConnectionPool() {\n        // Khoi tao cac ket noi co so du lieu\n    }\n\n    public static DatabaseConnectionPool getInstance() {\n        if (instance == null) {\n            synchronized (DatabaseConnectionPool.class) {\n                if (instance == null) {\n                    instance = new DatabaseConnectionPool();\n                }\n            }\n        }\n        return instance;\n    }\n}",
                List.of(
                        "Đảm bảo tính duy nhất của thực thể",
                        "Tiết kiệm tài nguyên bộ nhớ",
                        "Khởi tạo lười (Lazy initialization)"
                ),
                List.of(
                        "Vi phạm nguyên lý đơn trách nhiệm (SRP)",
                        "Khó viết Unit Test do trạng thái toàn cục",
                        "Phức tạp trong môi trường đa luồng nếu không khóa cẩn thận"
                ),
                List.of(
                        "Spring Bean scope 'singleton' (mặc định trong ApplicationContext)",
                        "Runtime.getRuntime() trong Java Core",
                        "Desktop.getDesktop()"
                )
        ));

        registerPattern(new DesignPatternItem(
                "PAT-02",
                "Factory Method Pattern",
                "Mẫu phương thức nhà máy",
                "CREATIONAL",
                "Định nghĩa một giao diện để tạo một đối tượng, nhưng để các lớp con quyết định lớp nào sẽ được tạo thực thể.",
                "Tách rời mã khởi tạo đối tượng khỏi mã sử dụng đối tượng, tuân thủ nguyên lý Open/Closed Principle.",
                "+-----------------+\n|     Creator     |\n+-----------------+\n| + create(): Prod|\n+--------^--------+\n         |\n+--------+--------+\n| ConcreteCreator |\n+-----------------+",
                "public interface NotificationSender {\n    void send(String recipient, String message);\n}\n\npublic class EmailNotificationSender implements NotificationSender {\n    public void send(String recipient, String message) {\n        System.out.println(\"Gui email toi \" + recipient);\n    }\n}\n\npublic abstract class NotificationService {\n    public abstract NotificationSender createSender();\n    public void notifyUser(String recipient, String msg) {\n        NotificationSender sender = createSender();\n        sender.send(recipient, msg);\n    }\n}",
                List.of(
                        "Tránh sự gắn kết chặt chẽ giữa creator và concrete products",
                        "Dễ dàng mở rộng thêm product mới mà không sửa mã nguồn cũ"
                ),
                List.of(
                        "Số lượng lớp tăng lên đáng kể vì mỗi product cần một creator con"
                ),
                List.of(
                        "LoggerFactory.getLogger() trong SLF4J",
                        "Calendar.getInstance()",
                        "ResourceBundle.getBundle()"
                )
        ));

        registerPattern(new DesignPatternItem(
                "PAT-03",
                "Abstract Factory Pattern",
                "Mẫu nhà máy trừu tượng",
                "CREATIONAL",
                "Cung cấp một giao diện để tạo các họ đối tượng có liên quan hoặc phụ thuộc lẫn nhau mà không chỉ định các lớp cụ thể của chúng.",
                "Hỗ trợ đa nền tảng hoặc đa giao diện người dùng (ví dụ: giao diện DarkMode và LightMode với trọn bộ Button, Checkbox, TextField).",
                "+-------------------+\n|    GUIFactory     |\n+-------------------+\n| + createButton()  |\n| + createCheckbox()|\n+-------------------+",
                "public interface GUIFactory {\n    Button createButton();\n    Checkbox createCheckbox();\n}\n\npublic class DarkModeFactory implements GUIFactory {\n    public Button createButton() { return new DarkButton(); }\n    public Checkbox createCheckbox() { return new DarkCheckbox(); }\n}\n\npublic class LightModeFactory implements GUIFactory {\n    public Button createButton() { return new LightButton(); }\n    public Checkbox createCheckbox() { return new LightCheckbox(); }\n}",
                List.of(
                        "Đảm bảo tính tương thích giữa các sản phẩm trong cùng một họ",
                        "Cách ly mã client khỏi các lớp cụ thể"
                ),
                List.of(
                        "Mã trở nên phức tạp do phải tạo rất nhiều interface và lớp con"
                ),
                List.of(
                        "DocumentBuilderFactory.newInstance() trong javax.xml",
                        "JPA EntityManagerFactory",
                        "ConnectionFactory trong JMS"
                )
        ));

        registerPattern(new DesignPatternItem(
                "PAT-04",
                "Builder Pattern",
                "Mẫu người xây dựng",
                "CREATIONAL",
                "Tách biệt việc xây dựng một đối tượng phức tạp khỏi biểu diễn của nó, cho phép cùng một quy trình xây dựng tạo ra các biểu diễn khác nhau.",
                "Tránh Telescoping Constructor khi một đối tượng có từ 5-10 trường tùy chọn.",
                "+-------------------------+\n|      UserBuilder        |\n+-------------------------+\n| + withName(n): Builder  |\n| + withEmail(e): Builder |\n| + build(): User         |\n+-------------------------+",
                "public class UserProfile {\n    private final String username;\n    private final String email;\n    private final int age;\n\n    private UserProfile(Builder b) {\n        this.username = b.username;\n        this.email = b.email;\n        this.age = b.age;\n    }\n\n    public static class Builder {\n        private String username;\n        private String email;\n        private int age;\n\n        public Builder username(String u) { this.username = u; return this; }\n        public Builder email(String e) { this.email = e; return this; }\n        public Builder age(int a) { this.age = a; return this; }\n        public UserProfile build() { return new UserProfile(this); }\n    }\n}",
                List.of(
                        "Cho phép tạo các đối tượng từng bước một",
                        "Hỗ trợ tạo đối tượng bất biến (Immutable objects)",
                        "Code dễ đọc, dễ bảo trì"
                ),
                List.of(
                        "Cần tạo thêm lớp Builder riêng biệt làm tăng số lượng dòng mã"
                ),
                List.of(
                        "StringBuilder trong Java Core",
                        "Lombok @Builder",
                        "ResponseEntities trong Spring (ResponseEntity.ok().body(...))"
                )
        ));

        registerPattern(new DesignPatternItem(
                "PAT-05",
                "Prototype Pattern",
                "Mẫu nguyên mẫu",
                "CREATIONAL",
                "Chỉ định các loại đối tượng sẽ tạo bằng cách sử dụng một thực thể nguyên mẫu và tạo các đối tượng mới bằng cách sao chép nguyên mẫu này.",
                "Tiết kiệm chi phí khởi tạo khi việc tạo mới một đối tượng tốn nhiều tài nguyên xử lý hoặc truy vấn I/O phức tạp.",
                "+-------------------+\n|     Prototype     |\n+-------------------+\n| + clone(): Proto  |\n+-------------------+",
                "public interface CloneableCourse {\n    CloneableCourse cloneCourse();\n}\n\npublic class ExamTemplate implements CloneableCourse {\n    private String examName;\n    private List<String> questionIds = new ArrayList<>();\n\n    public ExamTemplate(String name) { this.examName = name; }\n\n    public ExamTemplate cloneCourse() {\n        ExamTemplate copy = new ExamTemplate(this.examName);\n        copy.questionIds.addAll(this.questionIds);\n        return copy;\n    }\n}",
                List.of(
                        "Nhân bản đối tượng nhanh chóng mà không phụ thuộc vào lớp cụ thể",
                        "Giảm thiểu chi phí khởi tạo tốn kém"
                ),
                List.of(
                        "Nhân bản sâu (Deep Clone) các đối tượng có quan hệ tham chiếu vòng rất phức tạp"
                ),
                List.of(
                        "java.lang.Cloneable",
                        "Spring Bean scope 'prototype'"
                )
        ));

        registerPattern(new DesignPatternItem(
                "PAT-06",
                "Adapter Pattern",
                "Mẫu tiếp hợp",
                "STRUCTURAL",
                "Chuyển đổi giao diện của một lớp thành một giao diện khác mà máy khách mong đợi. Giúp các lớp không tương thích có thể làm việc cùng nhau.",
                "Tích hợp thư viện bên thứ ba (Third-party SDK) hoặc hệ thống kế thừa (Legacy Code) mà không cần viết lại toàn bộ.",
                "+---------------+       +---------------+\n|    Target     |       |    Adaptee    |\n+---------------+       +---------------+\n| + request()   |       | + specReq()   |\n+-------^-------+       +-------^-------+\n        |                       |\n+-------+-------+               |\n|    Adapter    o---------------+",
                "public interface PaymentGateway {\n    void processCharge(double amount, String currency);\n}\n\npublic class LegacyVnPayService {\n    public void executeVnPayPayment(long amountInVnd) {\n        System.out.println(\"VNPay thanh toan: \" + amountInVnd + \" VND\");\n    }\n}\n\npublic class VnPayAdapter implements PaymentGateway {\n    private final LegacyVnPayService legacyService;\n\n    public VnPayAdapter(LegacyVnPayService s) { this.legacyService = s; }\n\n    public void processCharge(double amount, String currency) {\n        long vnd = (long) (amount * 25000);\n        legacyService.executeVnPayPayment(vnd);\n    }\n}",
                List.of(
                        "Nguyên lý đơn trách nhiệm và mở/đóng",
                        "Tái sử dụng mã nguồn thư viện bên ngoài dễ dàng"
                ),
                List.of(
                        "Tăng độ phức tạp tổng thể do cần thêm nhiều lớp chuyển đổi trung gian"
                ),
                List.of(
                        "Arrays.asList() chuyển mảng thành List",
                        "InputStreamReader chuyển byte stream thành char stream",
                        "Spring HandlerAdapter trong Spring MVC"
                )
        ));

        registerPattern(new DesignPatternItem(
                "PAT-07",
                "Bridge Pattern",
                "Mẫu cầu nối",
                "STRUCTURAL",
                "Tách biệt một sự trừu tượng hóa khỏi việc triển khai của nó để cả hai có thể thay đổi độc lập.",
                "Tránh hiện tượng bùng nổ số lượng lớp con khi một lớp có nhiều chiều kích biến thể (ví dụ: Shape x Color).",
                "+---------------+       +---------------+\n|  Abstraction  o------>|  Implementor  |\n+---------------+       +---------------+ ",
                "public interface MessageSenderBridge {\n    void sendRaw(String data);\n}\n\npublic class SmsSenderBridge implements MessageSenderBridge {\n    public void sendRaw(String d) { System.out.println(\"SMS: \" + d); }\n}\n\npublic abstract class SystemAlert {\n    protected final MessageSenderBridge bridge;\n    public SystemAlert(MessageSenderBridge b) { this.bridge = b; }\n    public abstract void alert(String msg);\n}\n\npublic class UrgentAlert extends SystemAlert {\n    public UrgentAlert(MessageSenderBridge b) { super(b); }\n    public void alert(String msg) { bridge.sendRaw(\"[URGENT] \" + msg); }\n}",
                List.of(
                        "Mở rộng trừu tượng và hiện thực độc lập",
                        "Ẩn chi tiết triển khai khỏi client"
                ),
                List.of(
                        "Kiến trúc phức tạp hơn nếu hệ thống quy mô nhỏ"
                ),
                List.of(
                        "JDBC Driver architecture (java.sql.Driver)",
                        "SLF4J kết hợp Logback/Log4j2"
                )
        ));

        registerPattern(new DesignPatternItem(
                "PAT-08",
                "Composite Pattern",
                "Mẫu tổ hợp",
                "STRUCTURAL",
                "Sắp xếp các đối tượng thành cấu trúc dạng cây để đại diện cho hệ thống phân cấp một phần - toàn bộ. Client xử lý các đối tượng đơn lẻ và tổ hợp đồng nhất.",
                "Mô hình hóa hệ thống tập tin (File và Directory), cây cấu trúc tổ chức phòng ban doanh nghiệp.",
                "+-----------------+\n|    Component    |\n+-----------------+\n| + operation()   |\n+--------^--------+\n    /         \\\nLeaf       Composite",
                "public interface FileSystemNode {\n    long getSize();\n    void printHierarchy(String indent);\n}\n\npublic class FileNode implements FileSystemNode {\n    private final String name;\n    private final long size;\n    public FileNode(String n, long s) { this.name = n; this.size = s; }\n    public long getSize() { return size; }\n    public void printHierarchy(String indent) { System.out.println(indent + \"- \" + name + \" (\" + size + \" bytes)\"); }\n}\n\npublic class FolderNode implements FileSystemNode {\n    private final String name;\n    private final List<FileSystemNode> children = new ArrayList<>();\n    public FolderNode(String n) { this.name = n; }\n    public void add(FileSystemNode node) { children.add(node); }\n    public long getSize() { return children.stream().mapToLong(FileSystemNode::getSize).sum(); }\n    public void printHierarchy(String indent) {\n        System.out.println(indent + \"+ [\" + name + \"]\");\n        children.forEach(c -> c.printHierarchy(indent + \"  \"));\n    }\n}",
                List.of(
                        "Đơn giản hóa mã client bằng cách đồng nhất đối tượng đơn lẻ và tập hợp",
                        "Dễ dàng thêm các loại thành phần mới"
                ),
                List.of(
                        "Khó giới hạn các loại thành phần con trong một tổ hợp"
                ),
                List.of(
                        "Cây DOM trong XML/HTML",
                        "AWT/Swing Container và Component"
                )
        ));

        registerPattern(new DesignPatternItem(
                "PAT-09",
                "Decorator Pattern",
                "Mẫu trang trí",
                "STRUCTURAL",
                "Đính kèm các trách nhiệm bổ sung cho một đối tượng một cách động. Decorator cung cấp một sự thay thế linh hoạt cho việc tạo lớp con để mở rộng chức năng.",
                "Mở rộng hành vi của một lớp trong runtime mà không làm ảnh hưởng đến các đối tượng khác cùng lớp.",
                "+---------------------+\n|      Component      |\n+---------------------+\n| + execute(): void   |\n+----------^----------+\n           |\n+----------+----------+\n|  DecoratorWrapper   |\n+---------------------+",
                "public interface HttpDataSource {\n    String readData();\n}\n\npublic class SimpleHttpDataSource implements HttpDataSource {\n    public String readData() { return \"Raw Data\"; }\n}\n\npublic class CompressionDecorator implements HttpDataSource {\n    private final HttpDataSource wrappee;\n    public CompressionDecorator(HttpDataSource w) { this.wrappee = w; }\n    public String readData() { return \"GZIP_COMPRESSED(\" + wrappee.readData() + \")\"; }\n}\n\npublic class EncryptionDecorator implements HttpDataSource {\n    private final HttpDataSource wrappee;\n    public EncryptionDecorator(HttpDataSource w) { this.wrappee = w; }\n    public String readData() { return \"AES_ENCRYPTED(\" + wrappee.readData() + \")\"; }\n}",
                List.of(
                        "Mở rộng hành vi linh hoạt hơn kế thừa",
                        "Kết hợp nhiều hành vi độc lập lại với nhau"
                ),
                List.of(
                        "Khó loại bỏ một lớp bọc trang trí cụ thể ra khỏi ngăn xếp wrapper"
                ),
                List.of(
                        "Java I/O: BufferedReader(new FileReader(...))",
                        "Spring Security AuthenticationFilter chain"
                )
        ));

        registerPattern(new DesignPatternItem(
                "PAT-10",
                "Facade Pattern",
                "Mẫu mặt tiền",
                "STRUCTURAL",
                "Cung cấp một giao diện thống nhất, đơn giản hóa cho một tập hợp các giao diện trong một hệ thống con phức tạp.",
                "Giảm thiểu sự phụ thuộc giữa client và một hệ thống phụ trợ gồm hàng chục lớp và quy trình phức tạp.",
                "+-------------------------+\n|      OrderFacade        |\n+-------------------------+\n| + placeOrder(item, user)|\n+-------------------------+",
                "public class PaymentSubsystem { public void pay() {} }\npublic class InventorySubsystem { public void deduct() {} }\npublic class ShippingSubsystem { public void ship() {} }\n\npublic class OrderProcessingFacade {\n    private final PaymentSubsystem payment = new PaymentSubsystem();\n    private final InventorySubsystem inventory = new InventorySubsystem();\n    private final ShippingSubsystem shipping = new ShippingSubsystem();\n\n    public boolean completeOrder(String item, String user) {\n        inventory.deduct();\n        payment.pay();\n        shipping.ship();\n        return true;\n    }\n}",
                List.of(
                        "Cách ly client khỏi các thành phần con phức tạp",
                        "Giảm sự phụ thuộc liên kết giữa các module"
                ),
                List.of(
                        "Facade có thể trở thành 'God Object' nếu không phân tách hợp lý"
                ),
                List.of(
                        "Spring WebClient",
                        "Spring RestTemplate",
                        "Slf4j Logger API"
                )
        ));

        registerPattern(new DesignPatternItem(
                "PAT-11",
                "Flyweight Pattern",
                "Mẫu trọng lượng nhẹ",
                "STRUCTURAL",
                "Chia sẻ trạng thái chung giữa nhiều đối tượng để tiết kiệm bộ nhớ RAM khi phải quản lý hàng triệu đối tượng nhỏ.",
                "Mô hình hóa các ký tự văn bản trong Text Editor hoặc hàng vạn hạt phân tử trong Game Engine.",
                "+------------------------+\n|    FlyweightFactory    |\n+------------------------+\n| + getFlyweight(key)    |\n+------------------------+",
                "public record ParticleStyle(String color, String spriteTexture) {}\n\npublic class ParticleStyleFactory {\n    private static final Map<String, ParticleStyle> styles = new HashMap<>();\n\n    public static ParticleStyle getStyle(String color, String texture) {\n        String key = color + \"_\" + texture;\n        return styles.computeIfAbsent(key, k -> new ParticleStyle(color, texture));\n    }\n}\n\npublic class Particle {\n    private int x, y, speed;\n    private ParticleStyle style; // Shared flyweight\n    public Particle(int x, int y, int s, ParticleStyle style) {\n        this.x = x; this.y = y; this.speed = s; this.style = style;\n    }\n}",
                List.of(
                        "Tiết kiệm một lượng lớn bộ nhớ RAM"
                ),
                List.of(
                        "Tăng độ phức tạp mã nguồn do phải phân tách trạng thái nội tại (intrinsic) và ngoại lai (extrinsic)"
                ),
                List.of(
                        "Integer.valueOf() cache từ -128 đến 127",
                        "String Constant Pool trong Java JVM"
                )
        ));

        registerPattern(new DesignPatternItem(
                "PAT-12",
                "Proxy Pattern",
                "Mẫu đại diện",
                "STRUCTURAL",
                "Cung cấp một đối tượng đại diện để kiểm soát quyền truy cập, ghi log, hoặc tải lười (lazy loading) cho một đối tượng khác.",
                "Spring AOP, Hibernate Lazy Loading, Bảo mật phân quyền truy cập phương thức.",
                "+-------------------+\n|      Subject      |\n+-------------------+\n| + request(): void |\n+---------^---------+\n    /           \\\nRealSubject     Proxy",
                "public interface VideoDownloader {\n    byte[] downloadVideo(String videoUrl);\n}\n\npublic class RealVideoDownloader implements VideoDownloader {\n    public byte[] downloadVideo(String url) {\n        System.out.println(\"Tai video tu may chu tu xa: \" + url);\n        return new byte[1024];\n    }\n}\n\npublic class CachedVideoProxy implements VideoDownloader {\n    private final VideoDownloader target;\n    private final Map<String, byte[]> cache = new HashMap<>();\n\n    public CachedVideoProxy(VideoDownloader target) { this.target = target; }\n\n    public byte[] downloadVideo(String url) {\n        return cache.computeIfAbsent(url, target::downloadVideo);\n    }\n}",
                List.of(
                        "Kiểm soát quyền truy cập đối tượng thực tế",
                        "Hỗ trợ lazy loading và caching trong suốt đối với client"
                ),
                List.of(
                        "Làm tăng độ trễ phản hồi khi có nhiều tầng proxy bọc nhau"
                ),
                List.of(
                        "Spring @Transactional (Spring CGLIB / JDK Dynamic Proxy)",
                        "Hibernate Lazy Fetch (Bytecode instrumentation)"
                )
        ));

        registerPattern(new DesignPatternItem(
                "PAT-13",
                "Chain of Responsibility Pattern",
                "Mẫu chuỗi trách nhiệm",
                "BEHAVIORAL",
                "Tránh gắn kết bên gửi yêu cầu với bên nhận bằng cách cho nhiều đối tượng cơ hội xử lý yêu cầu. Chuyển tiếp yêu cầu dọc theo chuỗi.",
                "Hệ thống kiểm tra tính hợp lệ dữ liệu, kiểm tra quyền hạn đa tầng, Servlet Filter trong kiến trúc Web.",
                "+-------------------------+\n|       AuthHandler       |\n+-------------------------+\n| + handle(req): boolean  |\n| + setNext(h): Handler   |\n+-------------------------+",
                "public abstract class Middleware {\n    private Middleware next;\n\n    public Middleware linkWith(Middleware next) {\n        this.next = next;\n        return next;\n    }\n\n    public abstract boolean check(String email, String password);\n\n    protected boolean checkNext(String email, String password) {\n        if (next == null) return true;\n        return next.check(email, password);\n    }\n}\n\npublic class ThrottlingMiddleware extends Middleware {\n    public boolean check(String email, String password) {\n        System.out.println(\"Kiem tra rate limit...\");\n        return checkNext(email, password);\n    }\n}",
                List.of(
                        "Kiểm soát thứ tự xử lý yêu cầu",
                        "Tách rời người gửi yêu cầu khỏi người nhận yêu cầu"
                ),
                List.of(
                        "Một số yêu cầu có thể đi đến cuối chuỗi mà không được xử lý"
                ),
                List.of(
                        "javax.servlet.FilterChain",
                        "Spring Security Filter Chain",
                        "Log4j Logger hierarchy"
                )
        ));

        registerPattern(new DesignPatternItem(
                "PAT-14",
                "Command Pattern",
                "Mẫu lệnh",
                "BEHAVIORAL",
                "Đóng gói một yêu cầu dưới dạng một đối tượng, cho phép tham số hóa các máy khách với các yêu cầu khác nhau, xếp hàng hoặc ghi nhật ký các yêu cầu và hỗ trợ các thao tác có thể hoàn tác (Undo/Redo).",
                "Hệ thống chỉnh sửa văn bản với nút Ctrl+Z hoàn tác, hệ thống điều khiển hàng đợi công việc.",
                "+---------------------+\n|       Command       |\n+---------------------+\n| + execute(): void   |\n| + undo(): void      |\n+---------------------+",
                "public interface EditorCommand {\n    void execute();\n    void undo();\n}\n\npublic class InsertTextCommand implements EditorCommand {\n    private final StringBuilder document;\n    private final String textToInsert;\n\n    public InsertTextCommand(StringBuilder doc, String text) {\n        this.document = doc;\n        this.textToInsert = text;\n    }\n\n    public void execute() { document.append(textToInsert); }\n    public void undo() {\n        int len = textToInsert.length();\n        document.delete(document.length() - len, document.length());\n    }\n}",
                List.of(
                        "Tách rời đối tượng gọi hành động khỏi đối tượng thực thi hành động",
                        "Dễ dàng thêm tính năng Undo / Redo"
                ),
                List.of(
                        "Làm tăng số lượng lớp nhỏ rời rạc trong ứng dụng"
                ),
                List.of(
                        "java.lang.Runnable",
                        "Spring JdbcTemplate execute callbacks"
                )
        ));

        registerPattern(new DesignPatternItem(
                "PAT-15",
                "Iterator Pattern",
                "Mẫu lặp",
                "BEHAVIORAL",
                "Cung cấp một cách để truy cập tuần tự các phần tử của một đối tượng tập hợp mà không làm lộ biểu diễn cơ bản của nó.",
                "Duyệt danh sách liên kết, mảng động, cây nhị phân, hoặc đồ thị theo cùng cú pháp duyệt for-each.",
                "+----------------------+\n|       Iterator       |\n+----------------------+\n| + hasNext(): boolean |\n| + next(): E          |\n+----------------------+",
                "public class CustomCollection<T> implements Iterable<T> {\n    private final List<T> elements = new ArrayList<>();\n\n    public void add(T item) { elements.add(item); }\n\n    public Iterator<T> iterator() {\n        return new Iterator<>() {\n            private int cursor = 0;\n            public boolean hasNext() { return cursor < elements.size(); }\n            public T next() { return elements.get(cursor++); }\n        };\n    }\n}",
                List.of(
                        "Tuân thủ nguyên lý Single Responsibility",
                        "Duyệt tập hợp theo nhiều chiến lược khác nhau (BFS, DFS, Inorder)"
                ),
                List.of(
                        "Có thể không tối ưu so với duyệt chỉ số trực tiếp trên mảng đơn giản"
                ),
                List.of(
                        "java.util.Iterator",
                        "java.util.Enumeration",
                        "Iterable interface"
                )
        ));

        registerPattern(new DesignPatternItem(
                "PAT-16",
                "Observer Pattern",
                "Mẫu quan sát",
                "BEHAVIORAL",
                "Định nghĩa mối quan hệ phụ thuộc một-nhiều giữa các đối tượng để khi một đối tượng thay đổi trạng thái, tất cả các đối tượng phụ thuộc của nó sẽ được thông báo và cập nhật tự động.",
                "Hệ thống thông báo đẩy (Push Notifications), kiến trúc hướng sự kiện (Event-driven Architecture).",
                "+------------------------+\n|        Subject         |\n+------------------------+\n| + attach(Observer)     |\n| + notifyObservers()    |\n+------------------------+",
                "public interface CourseEventListener {\n    void onNewAssignment(String courseTitle, String assignmentTitle);\n}\n\npublic class CourseSubject {\n    private final List<CourseEventListener> listeners = new ArrayList<>();\n\n    public void subscribe(CourseEventListener l) { listeners.add(l); }\n    public void unsubscribe(CourseEventListener l) { listeners.remove(l); }\n\n    public void publishAssignment(String course, String assignment) {\n        listeners.forEach(l -> l.onNewAssignment(course, assignment));\n    }\n}",
                List.of(
                        "Quan hệ mở / đóng lỏng lẻo giữa người phát hành và người đăng ký sự kiện"
                ),
                List.of(
                        "Các observer có thể được thông báo theo thứ tự ngẫu nhiên"
                ),
                List.of(
                        "Spring ApplicationEventPublisher và @EventListener",
                        "Java PropertyChangeListener"
                )
        ));

        registerPattern(new DesignPatternItem(
                "PAT-17",
                "State Pattern",
                "Mẫu trạng thái",
                "BEHAVIORAL",
                "Cho phép một đối tượng thay đổi hành vi của nó khi trạng thái nội bộ của nó thay đổi. Đối tượng dường như thay đổi lớp của nó.",
                "Quản lý vòng đời đơn hàng (PENDING -> PAID -> SHIPPING -> DELIVERED), bài kiểm tra trực tuyến.",
                "+-----------------------+\n|       ExamState       |\n+-----------------------+\n| + submit(): void      |\n| + tick(): void        |\n+-----------------------+",
                "public interface ExamSessionState {\n    void handleAction(ExamContext ctx);\n}\n\npublic class InProgressState implements ExamSessionState {\n    public void handleAction(ExamContext ctx) {\n        System.out.println(\"Dang trong gio lam bai: Thu nhan cau tra loi...\");\n    }\n}\n\npublic class SubmittedState implements ExamSessionState {\n    public void handleAction(ExamContext ctx) {\n        System.out.println(\"Bai thi da nop! Khong cho phep sua them.\");\n    }\n}",
                List.of(
                        "Loại bỏ các câu lệnh switch-case hoặc if-else cồng kềnh",
                        "Dễ dàng thêm trạng thái mới mà không sửa logic cũ"
                ),
                List.of(
                        "Có thể quá mức cần thiết nếu máy trạng thái chỉ có 2-3 trạng thái đơn giản"
                ),
                List.of(
                        "Spring State Machine",
                        "Thread.State enum"
                )
        ));

        registerPattern(new DesignPatternItem(
                "PAT-18",
                "Strategy Pattern",
                "Mẫu chiến lược",
                "BEHAVIORAL",
                "Định nghĩa một họ các thuật toán, đóng gói từng thuật toán và làm cho chúng có thể hoán đổi cho nhau. Chiến lược cho phép thuật toán thay đổi độc lập với máy khách sử dụng nó.",
                "Hệ thống tính điểm thi, chiến lược giảm giá học phí, thuật toán nén tệp tin.",
                "+--------------------------+\n|      ScoreStrategy       |\n+--------------------------+\n| + compute(raw): double   |\n+--------------------------+",
                "public interface GradingStrategy {\n    double calculateGrade(List<Double> scores);\n}\n\npublic class AverageGradingStrategy implements GradingStrategy {\n    public double calculateGrade(List<Double> s) {\n        return s.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);\n    }\n}\n\npublic class HighestScoreGradingStrategy implements GradingStrategy {\n    public double calculateGrade(List<Double> s) {\n        return s.stream().mapToDouble(Double::doubleValue).max().orElse(0.0);\n    }\n}",
                List.of(
                        "Hoán đổi thuật toán linh hoạt trong runtime",
                        "Cô lập logic thuật toán phức tạp khỏi mã nghiệp vụ"
                ),
                List.of(
                        "Client phải biết rõ sự khác biệt giữa các chiến lược để lựa chọn"
                ),
                List.of(
                        "java.util.Comparator",
                        "Spring Security AuthenticationProvider"
                )
        ));

        registerPattern(new DesignPatternItem(
                "PAT-19",
                "Template Method Pattern",
                "Mẫu phương thức khuôn mẫu",
                "BEHAVIORAL",
                "Định nghĩa khung của một thuật toán trong một thao tác, hoãn lại một số bước cho các lớp con. Cho phép các lớp con định nghĩa lại một số bước mà không thay đổi cấu trúc của thuật toán.",
                "Quy trình chấm bài thi lập trình: 1. Biên dịch -> 2. Chạy test case -> 3. So sánh kết quả -> 4. Dọn dẹp tài nguyên sandbox.",
                "+-------------------------+\n|      BaseJudgeFlow      |\n+-------------------------+\n| + runPipeline() final   |\n| # compile() abstract    |\n| # execute() abstract    |\n+-------------------------+",
                "public abstract class AbstractCodeRunner {\n    public final void executeSubmission() {\n        prepareSandbox();\n        compileCode();\n        runTestCases();\n        cleanup();\n    }\n\n    private void prepareSandbox() { System.out.println(\"Tao thu muc co lap...\"); }\n    protected abstract void compileCode();\n    protected abstract void runTestCases();\n    private void cleanup() { System.out.println(\"Xoa tep tam.\"); }\n}",
                List.of(
                        "Tái sử dụng mã khung thuật toán tối đa",
                        "Kiểm soát các điểm mở rộng chặt chẽ"
                ),
                List.of(
                        "Bị giới hạn bởi bộ khung do lớp cha quy định"
                ),
                List.of(
                        "Spring JdbcTemplate, JmsTemplate",
                        "HttpServlet.service() gọi doGet/doPost"
                )
        ));

        registerPattern(new DesignPatternItem(
                "PAT-20",
                "Visitor Pattern",
                "Mẫu người ghé thăm",
                "BEHAVIORAL",
                "Tách biệt một thuật toán khỏi cấu trúc đối tượng mà nó hoạt động. Cho phép thêm các thao tác mới vào các cấu trúc đối tượng hiện có mà không cần sửa đổi các cấu trúc đó.",
                "Trình biên dịch phân tích cây cú pháp trừu tượng AST (Abstract Syntax Tree), bộ xuất dữ liệu báo cáo đa định dạng.",
                "+-------------------------+\n|         Visitor         |\n+-------------------------+\n| + visit(DocElement)     |\n+-------------------------+",
                "public interface AstVisitor {\n    void visit(BinaryExpression node);\n    void visit(LiteralNode node);\n}\n\npublic interface AstNode {\n    void accept(AstVisitor visitor);\n}\n\npublic class LiteralNode implements AstNode {\n    public final int value;\n    public LiteralNode(int v) { this.value = v; }\n    public void accept(AstVisitor v) { v.visit(this); }\n}",
                List.of(
                        "Dễ dàng thêm các thao tác tính toán mới trên cấu trúc dữ liệu phức tạp"
                ),
                List.of(
                        "Rất khó cập nhật khi cấu trúc các lớp phần tử thay đổi"
                ),
                List.of(
                        "Java FileVisitor",
                        "AnnotationProcessor AST visitors"
                )
        ));

        registerPattern(new DesignPatternItem(
                "PAT-21",
                "Repository Pattern",
                "Mẫu kho lưu trữ",
                "ENTERPRISE_ARCHITECTURAL",
                "Cung cấp một lớp trừu tượng trung gian giữa tầng truy cập dữ liệu và tầng nghiệp vụ miền (Domain Logic), mô phỏng việc truy cập tập hợp đối tượng trong bộ nhớ.",
                "Tách biệt hoàn toàn công nghệ lưu trữ (SQL, NoSQL, In-Memory) khỏi logic xử lý nghiệp vụ.",
                "+-----------------------+\n|      Repository       |\n+-----------------------+\n| + findById(id): T     |\n| + save(entity): T     |\n+-----------------------+",
                "public interface CourseRepository {\n    Optional<Object> findById(Long id);\n    void save(Object course);\n    List<Object> findByInstructorId(Long instructorId);\n}",
                List.of(
                        "Dễ viết Unit Test bằng cách Mock Repository",
                        "Dễ thay đổi công nghệ cơ sở dữ liệu"
                ),
                List.of(
                        "Có thể gây trùng lặp nếu ORM framework đã có tính năng tương tự"
                ),
                List.of(
                        "Spring Data JPA: JpaRepository, CrudRepository"
                )
        ));

        registerPattern(new DesignPatternItem(
                "PAT-22",
                "Unit of Work Pattern",
                "Mẫu đơn vị công việc",
                "ENTERPRISE_ARCHITECTURAL",
                "Duy trì danh sách các đối tượng bị ảnh hưởng bởi một giao dịch nghiệp vụ và điều phối việc ghi nhận các thay đổi cũng như xử lý các vấn đề đồng thời.",
                "Gộp nhiều thao tác thêm, sửa, xóa thành một giao dịch cơ sở dữ liệu duy nhất để tối ưu hiệu năng I/O.",
                "+--------------------------+\n|        UnitOfWork        |\n+--------------------------+\n| + registerNew(entity)    |\n| + registerDirty(entity)  |\n| + commit(): void         |\n+--------------------------+",
                "public class UnitOfWork {\n    private final List<Object> newEntities = new ArrayList<>();\n    private final List<Object> dirtyEntities = new ArrayList<>();\n\n    public void registerNew(Object e) { newEntities.add(e); }\n    public void registerDirty(Object e) { dirtyEntities.add(e); }\n\n    public void commitTransaction() {\n        System.out.println(\"Thuc hien batch INSERT cho \" + newEntities.size() + \" ban ghi\");\n        System.out.println(\"Thuc hien batch UPDATE cho \" + dirtyEntities.size() + \" ban ghi\");\n    }\n}",
                List.of(
                        "Giảm thiểu số lượng truy vấn mạng tới Database",
                        "Đảm bảo tính toàn vẹn ACID"
                ),
                List.of(
                        "Phức tạp khi cần rollback các trạng thái phức tạp trong bộ nhớ"
                ),
                List.of(
                        "Hibernate Session / JPA EntityManager persistence context"
                )
        ));

        registerPattern(new DesignPatternItem(
                "PAT-23",
                "CQRS Pattern",
                "Tách biệt trách nhiệm đọc và ghi",
                "ENTERPRISE_ARCHITECTURAL",
                "Tách biệt mô hình dữ liệu xử lý ghi (Commands) khỏi mô hình dữ liệu truy vấn đọc (Queries) để tối ưu hóa hiệu năng và khả năng mở rộng độc lập.",
                "Hệ thống có tỉ lệ đọc vượt trội (95% Read, 5% Write) như mạng xã hội, LMS xem điểm thi.",
                "+-----------------+        +-----------------+\n|  Command Model  |        |   Query Model   |\n|  (Write DB)     |--sync->|   (Read Cache)  |\n+-----------------+        +-----------------+",
                "public record CreateCourseCommand(String title, String instructor) {}\npublic record CourseSummaryQuery(String category) {}\n\npublic class CourseCommandHandler {\n    public void handle(CreateCourseCommand cmd) {\n        System.out.println(\"Ghi vao Master SQL DB: \" + cmd.title());\n    }\n}\n\npublic class CourseQueryHandler {\n    public List<Object> handle(CourseSummaryQuery q) {\n        System.out.println(\"Doc tu Redis Cache hoac Read Replica...\");\n        return List.of();\n    }\n}",
                List.of(
                        "Mở rộng độc lập cho tác vụ đọc và ghi",
                        "Mô hình dữ liệu tối ưu cho từng mục đích"
                ),
                List.of(
                        "Hiện tượng nhất quán cuối cùng (Eventual Consistency) cần được xử lý"
                ),
                List.of(
                        "Axon Framework",
                        "Spring Data + Elasticsearch read models"
                )
        ));

        registerPattern(new DesignPatternItem(
                "PAT-24",
                "Event Sourcing Pattern",
                "Mẫu nguồn sự kiện",
                "ENTERPRISE_ARCHITECTURAL",
                "Lưu trữ trạng thái của một thực thể dưới dạng một chuỗi các sự kiện thay đổi trạng thái bất biến theo thời gian thay vì chỉ lưu trạng thái hiện tại.",
                "Hệ thống ngân hàng kiểm toán, lịch sử chỉnh sửa điểm thi sinh viên cần minh bạch 100%.",
                "+---------------+       +---------------+       +---------------+\n| StudentCreated| ----> | CourseEnrolled| ----> | GradeRecorded |\n+---------------+       +---------------+       +---------------+",
                "public record StudentRegisteredEvent(String studentId, String timestamp) {}\npublic record CourseEnrolledEvent(String studentId, String courseId) {}\n\npublic class StudentAggregate {\n    private String id;\n    private final List<String> enrolledCourses = new ArrayList<>();\n\n    public void apply(StudentRegisteredEvent event) {\n        this.id = event.studentId();\n    }\n\n    public void apply(CourseEnrolledEvent event) {\n        this.enrolledCourses.add(event.courseId());\n    }\n}",
                List.of(
                        "Kiểm toán vết lịch sử hoàn hảo (Audit Trail)",
                        "Có thể tái tạo trạng thái hệ thống tại bất kỳ thời điểm nào"
                ),
                List.of(
                        "Đòi hỏi kỹ thuật Snapshot để tránh đọc lại hàng triệu sự kiện cũ"
                ),
                List.of(
                        "Apache Kafka event streams",
                        "EventStoreDB"
                )
        ));

        registerPattern(new DesignPatternItem(
                "PAT-25",
                "Saga Pattern",
                "Mẫu Saga điều phối giao dịch phân tán",
                "ENTERPRISE_ARCHITECTURAL",
                "Quản lý các giao dịch phân tán trải dài trên nhiều microservice thông qua một chuỗi các giao dịch cục bộ kèm các hành động bù trừ (Compensating Transactions) khi có sự cố.",
                "Quy trình đăng ký học: Trừ tiền học phí -> Giữ chỗ lớp học -> Cấp tài khoản portal.",
                "+-------------+        +-------------+        +-------------+\n| PaymentSvc  | -----> | SeatAllocSvc| -----> | AccountSvc  |\n+-------------+        +-------------+        +-------------+\n       |                      |\n       +--- Compensate <------+",
                "public class EnrollmentSagaOrchestrator {\n    public void executeEnrollment(String studentId, String courseId) {\n        try {\n            boolean charged = chargeTuition(studentId);\n            if (!charged) return;\n\n            boolean reserved = reserveSeat(courseId);\n            if (!reserved) {\n                refundTuition(studentId); // Compensation\n            }\n        } catch (Exception e) {\n            System.err.println(\"Giao dich that bai, kich hoat bu tru...\");\n        }\n    }\n    private boolean chargeTuition(String s) { return true; }\n    private boolean reserveSeat(String c) { return false; }\n    private void refundTuition(String s) { System.out.println(\"Hoan tien hoc phi\"); }\n}",
                List.of(
                        "Thực hiện giao dịch phân tán mà không cần khóa tài nguyên 2PC tốn kém"
                ),
                List.of(
                        "Thiết kế hành động bù trừ rất phức tạp"
                ),
                List.of(
                        "Camunda Workflow Engine",
                        "Temporal.io",
                        "Axon Saga"
                )
        ));

        registerPattern(new DesignPatternItem(
                "PAT-26",
                "Outbox Pattern",
                "Mẫu hộp thư gửi đi",
                "ENTERPRISE_ARCHITECTURAL",
                "Đảm bảo việc cập nhật cơ sở dữ liệu và gửi thông điệp vào Message Broker diễn ra nguyên tử (Atomic) bằng cách ghi thông điệp vào bảng Outbox trong cùng một transaction.",
                "Tránh mất dữ liệu hoặc gửi thông điệp sai khi dịch vụ bị crash giữa lúc ghi DB và publish Kafka.",
                "+------------------------+\n| Business Transaction   |\n| - Update Student Table |\n| - Insert Outbox Event  |\n+-----------+------------+\n            |\n            v\n     Outbox Poller ---> Message Broker",
                "public class OutboxService {\n    public void createOrderWithEvent(String orderId) {\n        // Mo transaction\n        // 1. Luu ban ghi don hang vao DB\n        // 2. Chen su kien OrderCreated vao bang OUTBOX_EVENTS\n        // Commit transaction\n    }\n\n    public void pollAndPublishOutbox() {\n        // Worker chay dinh ky doc tu OUTBOX_EVENTS va publish sang Kafka\n    }\n}",
                List.of(
                        "Đảm bảo tin cậy chuyển giao ít nhất một lần (At-least-once delivery)",
                        "Không cần phân tán giao dịch XA"
                ),
                List.of(
                        "Người nhận phải xử lý trùng lặp (Idempotent consumer)"
                ),
                List.of(
                        "Debezium CDC (Change Data Capture)",
                        "Spring Kafka Outbox pattern"
                )
        ));

        registerPattern(new DesignPatternItem(
                "PAT-27",
                "Circuit Breaker Pattern",
                "Mẫu ngắt mạch",
                "ENTERPRISE_ARCHITECTURAL",
                "Ngăn chặn hệ thống liên tục thực hiện một thao tác có khả năng thất bại cao khi dịch vụ phụ thuộc bên ngoài bị lỗi hoặc quá tải.",
                "Bảo vệ hệ thống LMS khỏi sự cố khi hệ thống thanh toán ngân hàng hoặc cổng SMS bên thứ ba bị sập.",
                "+----------+       Timeout/Fail > Threshold       +--------+\n|  CLOSED  | -----------------------------------> |  OPEN  |\n+----------+                                      +--------+\n     ^                                                |\n     |             Success Rate > Target              v\n     +------------------------------------------+ HALF-OPEN|",
                "public class SimpleCircuitBreaker {\n    private enum State { CLOSED, OPEN, HALF_OPEN }\n    private State state = State.CLOSED;\n    private int failureCount = 0;\n\n    public void recordFailure() {\n        failureCount++;\n        if (failureCount >= 5) {\n            state = State.OPEN;\n            System.out.println(\"Ngot mach! Tu choi cac yeu cau tiep theo.\");\n        }\n    }\n\n    public boolean allowRequest() {\n        return state != State.OPEN;\n    }\n}",
                List.of(
                        "Bảo vệ tài nguyên hệ thống khỏi hiệu ứng tuyết lở (Cascading Failure)",
                        "Phục hồi tự động khi dịch vụ khỏe lại"
                ),
                List.of(
                        "Cần xác định ngưỡng tham số phù hợp (Timeout, Failure Rate, Recovery Time)"
                ),
                List.of(
                        "Resilience4j",
                        "Netflix Hystrix",
                        "Spring Cloud Circuit Breaker"
                )
        ));

    }

    private static void registerPattern(DesignPatternItem entry) {
        PATTERNS.put(entry.id(), entry);
    }

    public static List<DesignPatternItem> getAllPatterns() {
        return List.copyOf(PATTERNS.values());
    }

    public static Optional<DesignPatternItem> findById(String id) {
        return Optional.ofNullable(PATTERNS.get(id));
    }

    public static List<DesignPatternItem> findByCategory(String category) {
        return PATTERNS.values().stream()
                .filter(p -> p.category().equalsIgnoreCase(category))
                .toList();
    }

    public static int getTotalPatternCount() {
        return PATTERNS.size();
    }
}
