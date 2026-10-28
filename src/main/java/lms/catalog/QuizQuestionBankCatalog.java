package lms.catalog;

import java.util.*;

/**
 * Ngân hàng Câu hỏi Khảo thí & Trắc nghiệm Chuẩn hóa (120 câu hỏi chọn lọc).
 * Phục vụ cho hệ thống Thi trực tuyến, Đánh giá sinh viên và Kiểm tra định kỳ.
 */
public final class QuizQuestionBankCatalog {

    public record QuestionOption(
            String key,
            String text,
            boolean isCorrect,
            String explanation
    ) {}

    public record QuizQuestionItem(
            String id,
            String subjectCode,
            String topic,
            String questionText,
            String questionType,
            String difficulty,
            List<QuestionOption> options,
            String detailedSolution
    ) {}

    private static final Map<String, QuizQuestionItem> QUESTIONS = new LinkedHashMap<>();

    static {
        registerQuestion(new QuizQuestionItem(
                "Q-001",
                "NET101",
                "Lập trình mạng",
                "Giao thức nào cung cấp kênh truyền tin cậy, hướng kết nối và kiểm soát luồng trong tầng Giao vận?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "TCP", true, "TCP thiết lập kết nối 3 bước (3-way handshake) và đảm bảo gói tin đến đúng thứ tự."),
                        new QuestionOption("B", "UDP", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "IP", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "ICMP", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "TCP thiết lập kết nối 3 bước (3-way handshake) và đảm bảo gói tin đến đúng thứ tự."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-002",
                "NET101",
                "Lập trình mạng",
                "Cổng mặc định (Default Port) chuẩn của giao thức HTTPS bảo mật là gì?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "443", true, "HTTPS sử dụng cổng 443 với giao thức mã hóa TLS/SSL."),
                        new QuestionOption("B", "80", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "8080", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "22", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "HTTPS sử dụng cổng 443 với giao thức mã hóa TLS/SSL."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-003",
                "NET101",
                "Lập trình mạng",
                "Mã trạng thái HTTP nào biểu thị tài nguyên được yêu cầu không tìm thấy trên máy chủ?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "404 Not Found", true, "404 Not Found là mã lỗi client khi URL không tồn tại."),
                        new QuestionOption("B", "500 Internal Error", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "403 Forbidden", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "301 Moved", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "404 Not Found là mã lỗi client khi URL không tồn tại."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-004",
                "NET101",
                "Lập trình mạng",
                "Thuật toán bắt tay nào được sử dụng để thiết lập kết nối TCP?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "Three-way Handshake (SYN, SYN-ACK, ACK)", true, "TCP sử dụng quy trình bắt tay 3 bước để đồng bộ số thứ tự sequence number."),
                        new QuestionOption("B", "Two-way Handshake", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "Four-way Handshake", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "Sliding Window", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "TCP sử dụng quy trình bắt tay 3 bước để đồng bộ số thứ tự sequence number."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-005",
                "NET101",
                "Lập trình mạng",
                "Giao thức nào cho phép phân giải địa chỉ tên miền thành địa chỉ IP?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "DNS (Domain Name System)", true, "DNS ánh xạ từ tên miền FQDN sang địa chỉ IP tương ứng."),
                        new QuestionOption("B", "DHCP", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "ARP", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "BGP", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "DNS ánh xạ từ tên miền FQDN sang địa chỉ IP tương ứng."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-006",
                "NET101",
                "Lập trình mạng",
                "Kỹ thuật Socket non-blocking trong Java NIO sử dụng thành phần nào để theo dõi nhiều kênh I/O?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "Selector", true, "Selector cho phép một luồng đơn theo dõi sự kiện I/O trên nhiều kênh SocketChannel."),
                        new QuestionOption("B", "Thread Pool", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "Future", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "Condition Variable", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "Selector cho phép một luồng đơn theo dõi sự kiện I/O trên nhiều kênh SocketChannel."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-007",
                "NET101",
                "Lập trình mạng",
                "Đơn vị dữ liệu (PDU) tại tầng Giao vận (Transport Layer) được gọi là gì?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "Segment hoặc Datagram", true, "Tầng giao vận đóng gói dữ liệu thành Segment (TCP) hoặc Datagram (UDP)."),
                        new QuestionOption("B", "Packet", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "Frame", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "Bit stream", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "Tầng giao vận đóng gói dữ liệu thành Segment (TCP) hoặc Datagram (UDP)."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-008",
                "NET101",
                "Lập trình mạng",
                "Giao thức WebSocket hoạt động dựa trên cơ chế nào để nâng cấp từ HTTP?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "HTTP Upgrade Request (Status 101)", true, "Client gửi header 'Upgrade: websocket' và Server phản hồi mã 101 Switching Protocols."),
                        new QuestionOption("B", "CONNECT Method", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "TLS Renegotiation", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "UDP Tunnel", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "Client gửi header 'Upgrade: websocket' và Server phản hồi mã 101 Switching Protocols."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-009",
                "NET101",
                "Lập trình mạng",
                "Đặc điểm nào sau đây KHÔNG phải của giao thức UDP?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "Có cơ chế truyền lại gói tin khi bị mất (Retransmission)", true, "UDP không hỗ trợ truyền lại gói tin bị mất, không kiểm soát luồng hay tắc nghẽn."),
                        new QuestionOption("B", "Không hướng kết nối (Connectionless)", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "Header nhẹ 8 bytes", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "Tốc độ truyền nhanh", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "UDP không hỗ trợ truyền lại gói tin bị mất, không kiểm soát luồng hay tắc nghẽn."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-010",
                "NET101",
                "Lập trình mạng",
                "Giải thuật kiểm soát tắc nghẽn của TCP gồm các giai đoạn nào?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "Slow Start, Congestion Avoidance, Fast Retransmit, Fast Recovery", true, "TCP Reno/Tahoe gồm 4 pha kinh điển để điều chỉnh cửa sổ cwnd."),
                        new QuestionOption("B", "Round Robin, Priority Queue", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "Token Bucket, Leaky Bucket", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "Flooding, Distance Vector", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "TCP Reno/Tahoe gồm 4 pha kinh điển để điều chỉnh cửa sổ cwnd."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-011",
                "NET101",
                "Lập trình mạng",
                "Header 'Content-Type: application/json' trong HTTP Request thuộc loại header nào?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "Entity / Representation Header", true, "Content-Type mô tả kiểu MIME của nội dung payload trong phần thân request."),
                        new QuestionOption("B", "Security Header", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "Routing Header", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "Proxy Header", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "Content-Type mô tả kiểu MIME của nội dung payload trong phần thân request."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-012",
                "NET101",
                "Lập trình mạng",
                "Giao thức HTTP/2 mang lại cải tiến vượt bậc nào so với HTTP/1.1?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "Multiplexing trên một kết nối TCP duy nhất", true, "HTTP/2 sử dụng cơ chế dồn kênh (Multiplexing) nhị phân trên cùng 1 kết nối TCP."),
                        new QuestionOption("B", "Bỏ định dạng text", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "Bắt buộc dùng UDP", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "Không hỗ trợ Cookies", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "HTTP/2 sử dụng cơ chế dồn kênh (Multiplexing) nhị phân trên cùng 1 kết nối TCP."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-013",
                "NET101",
                "Lập trình mạng",
                "Giao thức HTTP/3 chuyển sang hoạt động trên nền giao thức truyền vận nào?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "QUIC (trên nền UDP)", true, "HTTP/3 sử dụng giao thức QUIC chạy trên UDP để khắc phục Head-of-line blocking."),
                        new QuestionOption("B", "TCP BBR", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "SCTP", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "DCCP", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "HTTP/3 sử dụng giao thức QUIC chạy trên UDP để khắc phục Head-of-line blocking."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-014",
                "NET101",
                "Lập trình mạng",
                "Địa chỉ IP 192.168.1.1 thuộc dải địa chỉ IP loại nào?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "Địa chỉ IP riêng (Private IP Class C)", true, "192.168.0.0/16 được RFC 1918 dành riêng cho mạng cục bộ tư nhân."),
                        new QuestionOption("B", "Địa chỉ IP công cộng (Public IP)", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "Địa chỉ Loopback", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "Địa chỉ Multicast", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "192.168.0.0/16 được RFC 1918 dành riêng cho mạng cục bộ tư nhân."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-015",
                "NET101",
                "Lập trình mạng",
                "Kỹ thuật NAT (Network Address Translation) giải quyết vấn đề gì?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "Cho phép nhiều máy tính trong mạng LAN dùng chung 1 IP công cộng", true, "NAT chuyển đổi địa chỉ IP tư nhân sang địa chỉ IP công cộng duy nhất."),
                        new QuestionOption("B", "Mã hóa dữ liệu", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "Phân phối tải server", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "Tăng tốc độ truy cập", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "NAT chuyển đổi địa chỉ IP tư nhân sang địa chỉ IP công cộng duy nhất."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-016",
                "DSA102",
                "Cấu trúc dữ liệu & Giải thuật",
                "Độ phức tạp thời gian trung bình của thuật toán QuickSort là bao nhiêu?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "O(N log N)", true, "QuickSort có độ phức tạp trung bình O(N log N) khi chọn pivot ngẫu nhiên."),
                        new QuestionOption("B", "O(N^2)", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "O(N)", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "O(log N)", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "QuickSort có độ phức tạp trung bình O(N log N) khi chọn pivot ngẫu nhiên."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-017",
                "DSA102",
                "Cấu trúc dữ liệu & Giải thuật",
                "Cấu trúc dữ liệu nào hoạt động theo nguyên lý LIFO (Last In First Out)?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "Stack (Ngăn xếp)", true, "Ngăn xếp Stack tuân thủ nguyên lý vào sau ra trước."),
                        new QuestionOption("B", "Queue (Hàng đợi)", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "Deque", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "Priority Queue", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "Ngăn xếp Stack tuân thủ nguyên lý vào sau ra trước."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-018",
                "DSA102",
                "Cấu trúc dữ liệu & Giải thuật",
                "Cấu trúc dữ liệu nào cho phép tìm kiếm, thêm, xóa với chi phí trung bình O(1)?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "Hash Table (Bảng băm)", true, "Bảng băm sử dụng hàm băm ánh xạ khóa tới chỉ số mảng trong O(1)."),
                        new QuestionOption("B", "Binary Search Tree", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "Linked List", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "Sorted Array", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "Bảng băm sử dụng hàm băm ánh xạ khóa tới chỉ số mảng trong O(1)."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-019",
                "DSA102",
                "Cấu trúc dữ liệu & Giải thuật",
                "Cây tìm kiếm nhị phân cân bằng AVL đảm bảo độ cao chênh lệch giữa 2 cây con không quá bao nhiêu?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "1", true, "Cây AVL duy trì hệ số cân bằng Balance Factor trong tập {-1, 0, 1}."),
                        new QuestionOption("B", "0", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "2", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "log N", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "Cây AVL duy trì hệ số cân bằng Balance Factor trong tập {-1, 0, 1}."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-020",
                "DSA102",
                "Cấu trúc dữ liệu & Giải thuật",
                "Độ phức tạp thời gian tìm kiếm phần tử trong cây Red-Black Tree với N nút là bao nhiêu?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "O(log N)", true, "Red-Black Tree đảm bảo chiều cao tối đa 2 * log(N + 1), nên tìm kiếm luôn là O(log N)."),
                        new QuestionOption("B", "O(N)", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "O(1)", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "O(N log N)", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "Red-Black Tree đảm bảo chiều cao tối đa 2 * log(N + 1), nên tìm kiếm luôn là O(log N)."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-021",
                "DSA102",
                "Cấu trúc dữ liệu & Giải thuật",
                "Thuật toán sắp xếp nào sau đây có tính ổn định (Stable Sort)?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "MergeSort", true, "MergeSort giữ nguyên thứ tự tương đối của các phần tử có cùng giá trị khóa."),
                        new QuestionOption("B", "QuickSort", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "HeapSort", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "SelectionSort", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "MergeSort giữ nguyên thứ tự tương đối của các phần tử có cùng giá trị khóa."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-022",
                "DSA102",
                "Cấu trúc dữ liệu & Giải thuật",
                "Đồ thị vô hướng liên thông có N đỉnh và cây khung (Spanning Tree) có bao nhiêu cạnh?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "N - 1 cạnh", true, "Cây khung luôn có đúng N đỉnh và N - 1 cạnh không chứa chu trình."),
                        new QuestionOption("B", "N cạnh", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "N + 1 cạnh", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "2N cạnh", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "Cây khung luôn có đúng N đỉnh và N - 1 cạnh không chứa chu trình."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-023",
                "DSA102",
                "Cấu trúc dữ liệu & Giải thuật",
                "Cấu trúc dữ liệu Heap hỗ trợ thao tác lấy phần tử nhỏ nhất (extractMin) trong thời gian bao lâu?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "O(log N)", true, "Trích xuất gốc và vun đống lại (heapify) mất thời gian tỷ lệ thuận với chiều cao O(log N)."),
                        new QuestionOption("B", "O(1)", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "O(N)", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "O(N log N)", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "Trích xuất gốc và vun đống lại (heapify) mất thời gian tỷ lệ thuận với chiều cao O(log N)."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-024",
                "DSA102",
                "Cấu trúc dữ liệu & Giải thuật",
                "Để tìm đường đi ngắn nhất trên đồ thị KHÔNG có trọng số, thuật toán nào tối ưu nhất?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "BFS (Breadth-First Search)", true, "BFS duyệt theo từng lớp khoảng cách nên tìm được đường đi ít cạnh nhất trong O(V + E)."),
                        new QuestionOption("B", "Dijkstra", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "Bellman-Ford", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "DFS", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "BFS duyệt theo từng lớp khoảng cách nên tìm được đường đi ít cạnh nhất trong O(V + E)."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-025",
                "DSA102",
                "Cấu trúc dữ liệu & Giải thuật",
                "Giải thuật tham lam Dijkstra không thể áp dụng chính xác cho đồ thị nào?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "Đồ thị có cạnh mang trọng số âm", true, "Trọng số âm có thể làm sai tiền đề tham lam của Dijkstra."),
                        new QuestionOption("B", "Đồ thị có hướng", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "Đồ thị có chu trình dương", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "Đồ thị thưa", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "Trọng số âm có thể làm sai tiền đề tham lam của Dijkstra."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-026",
                "DSA102",
                "Cấu trúc dữ liệu & Giải thuật",
                "Độ phức tạp không gian (Space Complexity) của thuật toán MergeSort trên mảng là bao nhiêu?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "O(N)", true, "MergeSort cần một mảng phụ kích thước N để trộn hai nửa con."),
                        new QuestionOption("B", "O(1)", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "O(log N)", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "O(N^2)", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "MergeSort cần một mảng phụ kích thước N để trộn hai nửa con."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-027",
                "DSA102",
                "Cấu trúc dữ liệu & Giải thuật",
                "Thuật toán Kadane dùng để giải quyết bài toán nào trong thời gian O(N)?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "Maximum Subarray Sum (Dãy con liên tiếp có tổng lớn nhất)", true, "Kadane duyệt 1 lần và duy trì tổng lớn nhất kết thúc tại vị trí hiện tại."),
                        new QuestionOption("B", "Longest Increasing Subsequence", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "Knapsack Problem", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "Shortest Path", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "Kadane duyệt 1 lần và duy trì tổng lớn nhất kết thúc tại vị trí hiện tại."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-028",
                "DSA102",
                "Cấu trúc dữ liệu & Giải thuật",
                "Độ dài của chuỗi con chung dài nhất (LCS) giữa hai chuỗi độ dài M và N giải bằng DP mất bao lâu?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "O(M * N)", true, "Bảng quy hoạch động có kích thước (M+1) x (N+1)."),
                        new QuestionOption("B", "O(M + N)", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "O(2^(M+N))", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "O(log(M+N))", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "Bảng quy hoạch động có kích thước (M+1) x (N+1)."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-029",
                "DSA102",
                "Cấu trúc dữ liệu & Giải thuật",
                "Cấu trúc dữ liệu Disjoint Set Union (DSU) với Union by Rank và Path Compression đạt độ phức tạp nào?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "O(alpha(N)) gần như hằng số", true, "Hàm Ackermann nghịch đảo alpha(N) <= 4 với mọi N thực tế trong vũ trụ."),
                        new QuestionOption("B", "O(log N)", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "O(N)", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "O(1)", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "Hàm Ackermann nghịch đảo alpha(N) <= 4 với mọi N thực tế trong vũ trụ."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-030",
                "DSA102",
                "Cấu trúc dữ liệu & Giải thuật",
                "Thuật toán Floyd-Warshall tìm đường đi ngắn nhất giữa mọi cặp đỉnh có độ phức tạp thời gian là gì?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "O(V^3)", true, "Ba vòng lặp lồng nhau qua tất cả các đỉnh trung gian k, i, j."),
                        new QuestionOption("B", "O(V^2)", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "O(V * E)", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "O(E log V)", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "Ba vòng lặp lồng nhau qua tất cả các đỉnh trung gian k, i, j."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-031",
                "OS103",
                "Hệ điều hành",
                "Thành phần nào của Hệ điều hành chịu trách nhiệm chuyển đổi ngữ cảnh (Context Switch)?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "Scheduler / Dispatcher", true, "Dispatcher lưu ngữ cảnh tiến trình cũ và nạp ngữ cảnh tiến trình mới vào CPU."),
                        new QuestionOption("B", "Compiler", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "Linker", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "Loader", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "Dispatcher lưu ngữ cảnh tiến trình cũ và nạp ngữ cảnh tiến trình mới vào CPU."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-032",
                "OS103",
                "Hệ điều hành",
                "Điều kiện nào sau đây KHÔNG phải là một trong bốn điều kiện gây ra bế tắc (Deadlock)?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "Preemption (Cho phép chiếm dụng)", true, "Bốn điều kiện của Coffman là: Mutual Exclusion, Hold and Wait, No Preemption, Circular Wait."),
                        new QuestionOption("B", "Mutual Exclusion", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "Hold and Wait", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "Circular Wait", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "Bốn điều kiện của Coffman là: Mutual Exclusion, Hold and Wait, No Preemption, Circular Wait."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-033",
                "OS103",
                "Hệ điều hành",
                "Thuật toán lập lịch CPU nào có thể dẫn đến hiện tượng đói tài nguyên (Starvation)?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "Shortest Job First (SJF)", true, "SJF ưu tiên tiến trình ngắn, nếu liên tục có tiến trình ngắn đến thì tiến trình dài sẽ bị đói."),
                        new QuestionOption("B", "Round Robin", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "First-Come First-Served", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "FIFO", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "SJF ưu tiên tiến trình ngắn, nếu liên tục có tiến trình ngắn đến thì tiến trình dài sẽ bị đói."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-034",
                "OS103",
                "Hệ điều hành",
                "Hiện tượng Trashing trong quản lý bộ nhớ ảo xảy ra khi nào?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "Hệ thống dành phần lớn thời gian tráo đổi trang (Page Swapping) thay vì thực thi lệnh", true, "Khi tổng tập làm việc (Working Set) vượt quá kích thước RAM thực tế."),
                        new QuestionOption("B", "RAM bị hỏng vật lý", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "Ổ đĩa cứng bị đầy", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "CPU quá nóng", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "Khi tổng tập làm việc (Working Set) vượt quá kích thước RAM thực tế."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-035",
                "OS103",
                "Hệ điều hành",
                "Cấu trúc dữ liệu nào trong nhân HĐH lưu trữ toàn bộ thông tin của một tiến trình?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "PCB (Process Control Block)", true, "PCB lưu PID, thanh ghi CPU, trạng thái tiến trình, con trỏ ngăn xếp và danh sách file mở."),
                        new QuestionOption("B", "TCB", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "Inode", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "Page Table", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "PCB lưu PID, thanh ghi CPU, trạng thái tiến trình, con trỏ ngăn xếp và danh sách file mở."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-036",
                "OS103",
                "Hệ điều hành",
                "Lời gọi hệ thống (System Call) fork() trong hệ điều hành Unix/Linux có tác dụng gì?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "Tạo một tiến trình con là bản sao của tiến trình cha", true, "fork() nhân bản tiến trình cha thành tiến trình con với PID mới."),
                        new QuestionOption("B", "Khởi chạy luồng mới", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "Thoát tiến trình", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "Cấp phát bộ nhớ", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "fork() nhân bản tiến trình cha thành tiến trình con với PID mới."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-037",
                "OS103",
                "Hệ điều hành",
                "Cơ chế đồng bộ hóa Semaphore nhị phân có giá trị nằm trong khoảng nào?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "0 và 1", true, "Binary Semaphore tương đương với Mutex lock."),
                        new QuestionOption("B", "Bất kỳ số nguyên không âm nào", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "Chỉ mang số âm", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "Không giới hạn", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "Binary Semaphore tương đương với Mutex lock."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-038",
                "OS103",
                "Hệ điều hành",
                "Bộ nhớ ảo (Virtual Memory) mang lại lợi ích chủ yếu nào sau đây?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "Cho phép chạy các chương trình có kích thước lớn hơn dung lượng RAM vật lý", true, "Bộ nhớ ảo sử dụng kỹ thuật phân trang (Paging) lưu trên ổ đĩa để mở rộng không gian địa chỉ."),
                        new QuestionOption("B", "Tăng tốc độ xung nhịp CPU", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "Bảo vệ đĩa cứng khỏi virus", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "Tiết kiệm điện năng", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "Bộ nhớ ảo sử dụng kỹ thuật phân trang (Paging) lưu trên ổ đĩa để mở rộng không gian địa chỉ."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-039",
                "OS103",
                "Hệ điều hành",
                "Thuật toán thay thế trang (Page Replacement) nào được coi là tối ưu về mặt lý thuyết?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "OPT / MIN (Bỏ trang không dùng trong tương lai xa nhất)", true, "Thuật toán tối ưu của Belady thay thế trang lâu được sử dụng nhất trong tương lai."),
                        new QuestionOption("B", "FIFO", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "LRU", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "Clock", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "Thuật toán tối ưu của Belady thay thế trang lâu được sử dụng nhất trong tương lai."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-040",
                "OS103",
                "Hệ điều hành",
                "Kỹ thuật Copy-on-Write (COW) trong fork() mang lại lợi ích gì?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "Tiết kiệm bộ nhớ bằng cách chỉ nhân bản trang bộ nhớ khi một trong hai tiến trình ghi dữ liệu", true, "Hai tiến trình chia sẻ chung trang bộ nhớ cho đến khi có thao tác ghi xảy ra."),
                        new QuestionOption("B", "Tăng tốc độ mạng", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "Chống deadlock", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "Bảo mật file", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "Hai tiến trình chia sẻ chung trang bộ nhớ cho đến khi có thao tác ghi xảy ra."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-041",
                "OS103",
                "Hệ điều hành",
                "Inode trong hệ thống tập tin Linux lưu trữ thông tin gì?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "Metadata của tập tin (quyền hạn, kích thước, thời gian, con trỏ khối dữ liệu)", true, "Tên tập tin được lưu trong Directory entry, còn Inode lưu metadata và vị trí các khối đĩa."),
                        new QuestionOption("B", "Tên của tập tin", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "Nội dung dữ liệu thực tế", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "Mật khẩu người dùng", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "Tên tập tin được lưu trong Directory entry, còn Inode lưu metadata và vị trí các khối đĩa."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-042",
                "OS103",
                "Hệ điều hành",
                "Mô hình đa luồng nào phổ biến nhất trong các hệ điều hành hiện đại như Linux và Windows?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "One-to-One (1 Kernel Thread ánh xạ 1 User Thread)", true, "Mô hình One-to-One cho phép luồng chạy song song thực sự trên CPU đa nhân."),
                        new QuestionOption("B", "Many-to-One", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "Many-to-Many", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "Two-level", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "Mô hình One-to-One cho phép luồng chạy song song thực sự trên CPU đa nhân."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-043",
                "OS103",
                "Hệ điều hành",
                "Tín hiệu (Signal) nào trong Linux KHÔNG thể bị chặn hoặc bắt giữ (Uncatchable)?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "SIGKILL (9) và SIGSTOP (19)", true, "SIGKILL được kernel xử lý ngay lập tức để tiêu diệt tiến trình mà không cho phép dọn dẹp."),
                        new QuestionOption("B", "SIGINT", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "SIGTERM", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "SIGSEGV", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "SIGKILL được kernel xử lý ngay lập tức để tiêu diệt tiến trình mà không cho phép dọn dẹp."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-044",
                "OS103",
                "Hệ điều hành",
                "Vấn đề nghịch đảo độ ưu tiên (Priority Inversion) được giải quyết bằng giao thức nào?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "Priority Inheritance Protocol (Thừa kế độ ưu tiên)", true, "Tiến trình độ ưu tiên thấp giữ tài nguyên sẽ tạm thời thừa kế độ ưu tiên cao của tiến trình đang đợi nó."),
                        new QuestionOption("B", "Round Robin", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "SJF", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "Banker's Algorithm", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "Tiến trình độ ưu tiên thấp giữ tài nguyên sẽ tạm thời thừa kế độ ưu tiên cao của tiến trình đang đợi nó."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-045",
                "OS103",
                "Hệ điều hành",
                "Giải thuật Nhà băng (Banker's Algorithm) của Dijkstra dùng để làm gì?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "Tránh bế tắc (Deadlock Avoidance) bằng cách kiểm tra trạng thái an toàn (Safe State)", true, "Banker kiểm tra xem việc cấp phát tài nguyên có duy trì trạng thái an toàn hay không."),
                        new QuestionOption("B", "Phát hiện Deadlock", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "Ngăn chặn Deadlock", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "Phục hồi Deadlock", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "Banker kiểm tra xem việc cấp phát tài nguyên có duy trì trạng thái an toàn hay không."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-046",
                "DB104",
                "Cơ sở dữ liệu",
                "Thuộc tính nào trong mô hình ACID đảm bảo rằng toàn bộ giao dịch được thực thi hoàn tất hoặc không có gì?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "Atomicity (Tính nguyên tử)", true, "All-or-nothing: Giao dịch thành công hoàn toàn hoặc rollback như chưa xảy ra."),
                        new QuestionOption("B", "Consistency", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "Isolation", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "Durability", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "All-or-nothing: Giao dịch thành công hoàn toàn hoặc rollback như chưa xảy ra."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-047",
                "DB104",
                "Cơ sở dữ liệu",
                "Mức độ cô lập (Isolation Level) nào cao nhất, ngăn chặn hoàn toàn Dirty Read, Non-repeatable Read và Phantom Read?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "Serializable", true, "Serializable tuần tự hóa các giao dịch tương đương chạy đơn luồng."),
                        new QuestionOption("B", "Repeatable Read", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "Read Committed", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "Read Uncommitted", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "Serializable tuần tự hóa các giao dịch tương đương chạy đơn luồng."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-048",
                "DB104",
                "Cơ sở dữ liệu",
                "Chỉ mục B+ Tree có ưu điểm vượt trội nào so với B-Tree truyền thống trong cơ sở dữ liệu?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "Tất cả dữ liệu nằm ở nút lá và các lá được liên kết tuần tự, tối ưu truy vấn khoảng (Range Scan)", true, "Nút lá liên kết đôi giúp duyệt dãy phần tử liên tiếp cực nhanh mà không cần duyệt cây ngược lên."),
                        new QuestionOption("B", "Chiều cao thấp hơn", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "Không cần cân bằng", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "Tốn ít bộ nhớ hơn", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "Nút lá liên kết đôi giúp duyệt dãy phần tử liên tiếp cực nhanh mà không cần duyệt cây ngược lên."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-049",
                "DB104",
                "Cơ sở dữ liệu",
                "Dạng chuẩn nào loại bỏ hoàn toàn các phụ thuộc hàm bắc cầu (Transitive Dependency)?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "Dạng chuẩn 3 (3NF)", true, "3NF yêu cầu thuộc tính không khóa không được phụ thuộc bắc cầu vào khóa chính."),
                        new QuestionOption("B", "Dạng chuẩn 1 (1NF)", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "Dạng chuẩn 2 (2NF)", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "BCNF", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "3NF yêu cầu thuộc tính không khóa không được phụ thuộc bắc cầu vào khóa chính."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-050",
                "DB104",
                "Cơ sở dữ liệu",
                "Hiện tượng Dirty Read trong cơ sở dữ liệu có nghĩa là gì?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "Một giao dịch đọc dữ liệu chưa được commit của một giao dịch khác", true, "Nếu giao dịch kia rollback thì dữ liệu đọc được là dữ liệu rác."),
                        new QuestionOption("B", "Dữ liệu bị xóa nhầm", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "Chỉ mục bị hỏng", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "Giao dịch bị timeout", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "Nếu giao dịch kia rollback thì dữ liệu đọc được là dữ liệu rác."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-051",
                "DB104",
                "Cơ sở dữ liệu",
                "Mục đích của việc đánh chỉ mục (Index) trên một cột trong cơ sở dữ liệu quan hệ là gì?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "Tăng tốc độ truy vấn SELECT tìm kiếm dữ liệu", true, "Index giúp tìm kiếm trong O(log N) thay vì quét toàn bộ bảng (Table Scan) O(N)."),
                        new QuestionOption("B", "Tăng tốc độ thao tác INSERT/UPDATE", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "Tiết kiệm dung lượng ổ đĩa", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "Tự động mã hóa cột", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "Index giúp tìm kiếm trong O(log N) thay vì quét toàn bộ bảng (Table Scan) O(N)."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-052",
                "DB104",
                "Cơ sở dữ liệu",
                "Khóa ngoại (Foreign Key) đảm bảo tính chất nào trong thiết kế cơ sở dữ liệu?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "Tính toàn vẹn tham chiếu (Referential Integrity)", true, "Ngăn chặn việc trỏ tới bản ghi cha không tồn tại."),
                        new QuestionOption("B", "Tính duy nhất", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "Tính nguyên tử", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "Tính phân tán", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "Ngăn chặn việc trỏ tới bản ghi cha không tồn tại."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-053",
                "DB104",
                "Cơ sở dữ liệu",
                "Lệnh SQL nào được sử dụng để xóa cấu trúc của toàn bộ bảng và dữ liệu khỏi cơ sở dữ liệu?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "DROP TABLE", true, "DROP TABLE loại bỏ cả định nghĩa schema lẫn dữ liệu trên đĩa."),
                        new QuestionOption("B", "DELETE FROM", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "TRUNCATE TABLE", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "REMOVE TABLE", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "DROP TABLE loại bỏ cả định nghĩa schema lẫn dữ liệu trên đĩa."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-054",
                "DB104",
                "Cơ sở dữ liệu",
                "Mệnh đề HAVING trong câu lệnh SQL khác với mệnh đề WHERE ở điểm nào?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "HAVING lọc dữ liệu sau khi gom nhóm (GROUP BY), áp dụng cho các hàm tổng hợp", true, "WHERE lọc từng dòng trước khi gom nhóm, còn HAVING lọc các nhóm kết quả tổng hợp."),
                        new QuestionOption("B", "HAVING nhanh hơn WHERE", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "HAVING lọc trước GROUP BY", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "HAVING chỉ dùng cho chuỗi", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "WHERE lọc từng dòng trước khi gom nhóm, còn HAVING lọc các nhóm kết quả tổng hợp."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-055",
                "DB104",
                "Cơ sở dữ liệu",
                "Kỹ thuật Sharding trong cơ sở dữ liệu phân tán có nghĩa là gì?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "Phân mảnh ngang dữ liệu trên nhiều máy chủ độc lập theo shard key", true, "Horizontal Partitioning giúp phân tải lưu trữ và tính toán khi dữ liệu vượt ngưỡng 1 máy."),
                        new QuestionOption("B", "Nhân bản dữ liệu read-replica", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "Sao lưu dự phòng", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "Nén dữ liệu", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "Horizontal Partitioning giúp phân tải lưu trữ và tính toán khi dữ liệu vượt ngưỡng 1 máy."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-056",
                "DB104",
                "Cơ sở dữ liệu",
                "Nhật ký ghi trước WAL (Write-Ahead Logging) đảm bảo thuộc tính nào trong ACID?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "Durability (Tính bền vững)", true, "Thay đổi được ghi vào WAL trên đĩa trước khi ghi vào trang bộ nhớ đệm data buffer."),
                        new QuestionOption("B", "Atomicity", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "Isolation", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "Consistency", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "Thay đổi được ghi vào WAL trên đĩa trước khi ghi vào trang bộ nhớ đệm data buffer."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-057",
                "DB104",
                "Cơ sở dữ liệu",
                "Cơ chế khóa lạc quan (Optimistic Locking) thường sử dụng thành phần nào để kiểm soát xung đột?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "Cột số phiên bản (@Version / Timestamp)", true, "Kiểm tra phiên bản lúc commit, nếu phiên bản đã thay đổi thì ném ngoại lệ OptimisticLockException."),
                        new QuestionOption("B", "SELECT FOR UPDATE", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "Table Lock", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "Shared Lock", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "Kiểm tra phiên bản lúc commit, nếu phiên bản đã thay đổi thì ném ngoại lệ OptimisticLockException."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-058",
                "DB104",
                "Cơ sở dữ liệu",
                "Phép kết nối (JOIN) nào trả về toàn bộ bản ghi từ cả 2 bảng, điền NULL nếu không khớp?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "FULL OUTER JOIN", true, "FULL JOIN kết hợp cả kết quả của LEFT JOIN và RIGHT JOIN."),
                        new QuestionOption("B", "INNER JOIN", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "LEFT JOIN", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "RIGHT JOIN", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "FULL JOIN kết hợp cả kết quả của LEFT JOIN và RIGHT JOIN."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-059",
                "DB104",
                "Cơ sở dữ liệu",
                "Giải pháp nào sau đây giúp giải quyết vấn đề hiệu năng chậm khi có quá nhiều lượt đọc (Read-heavy)?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "Thiết lập mô hình Master-Slave (Read Replicas) kết hợp bộ đệm Redis", true, "Master tiếp nhận thao tác ghi, đồng bộ sang nhiều Replica để san tải đọc."),
                        new QuestionOption("B", "Tăng kích thước cột", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "Chuyển sang dùng file CSV", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "Bỏ khóa ngoại", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "Master tiếp nhận thao tác ghi, đồng bộ sang nhiều Replica để san tải đọc."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-060",
                "DB104",
                "Cơ sở dữ liệu",
                "Nguyên lý NoSQL CAP khẳng định một hệ thống phân tán chỉ có thể đồng thời thỏa mãn tối đa mấy tính chất?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "2 trong 3 tính chất (Consistency, Availability, Partition Tolerance)", true, "Khi có phân vùng mạng (Partition), hệ thống phải đánh đổi giữa Nhất quán (CP) hoặc Sẵn sàng (AP)."),
                        new QuestionOption("B", "Cả 3 tính chất", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "Chỉ 1 tính chất duy nhất", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "Không giới hạn", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "Khi có phân vùng mạng (Partition), hệ thống phải đánh đổi giữa Nhất quán (CP) hoặc Sẵn sàng (AP)."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-061",
                "SE105",
                "Kỹ nghệ phần mềm & Kiến trúc",
                "Chữ 'S' trong bộ nguyên lý thiết kế SOLID đại diện cho nguyên lý nào?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "Single Responsibility Principle (Đơn trách nhiệm)", true, "Mỗi lớp hoặc module chỉ nên có duy nhất một lý do để thay đổi."),
                        new QuestionOption("B", "Software Design", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "Safe State", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "Static Binding", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "Mỗi lớp hoặc module chỉ nên có duy nhất một lý do để thay đổi."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-062",
                "SE105",
                "Kỹ nghệ phần mềm & Kiến trúc",
                "Nguyên lý Liskov Substitution Principle (LSP) yêu cầu điều gì?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "Các đối tượng của lớp con phải có thể thay thế cho các đối tượng của lớp cha mà không làm hỏng tính đúng đắn của chương trình", true, "Subtypes must be substitutable for their base types."),
                        new QuestionOption("B", "Lớp con phải kế thừa tất cả phương thức", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "Lớp cha phải là abstract", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "Không được ghi đè", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "Subtypes must be substitutable for their base types."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-063",
                "SE105",
                "Kỹ nghệ phần mềm & Kiến trúc",
                "Mẫu kiến trúc Microservices mang lại ưu điểm gì so với kiến trúc Monolithic?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "Khả năng triển khai độc lập, tự chủ công nghệ và mở rộng linh hoạt theo từng dịch vụ", true, "Mỗi dịch vụ sở hữu cơ sở dữ liệu riêng và được triển khai, cập nhật độc lập."),
                        new QuestionOption("B", "Đơn giản hơn trong việc kiểm thử", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "Không cần quản lý mạng phân tán", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "Giao dịch ACID dễ dàng", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "Mỗi dịch vụ sở hữu cơ sở dữ liệu riêng và được triển khai, cập nhật độc lập."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-064",
                "SE105",
                "Kỹ nghệ phần mềm & Kiến trúc",
                "Trong mô hình Scrum, cuộc họp Daily Scrum thường kéo dài tối đa bao nhiêu phút?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "15 phút", true, "Daily Standup là cuộc họp ngắn gọn hàng ngày tối đa 15 phút tập trung vào tiến độ và trở ngại."),
                        new QuestionOption("B", "30 phút", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "45 phút", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "60 phút", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "Daily Standup là cuộc họp ngắn gọn hàng ngày tối đa 15 phút tập trung vào tiến độ và trở ngại."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-065",
                "SE105",
                "Kỹ nghệ phần mềm & Kiến trúc",
                "Kiểm thử nào nhằm xác minh rằng sự thay đổi mã mới không phá vỡ các chức năng cũ đang hoạt động?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "Regression Testing (Kiểm thử hồi quy)", true, "Đảm bảo mã mới không gây ra lỗi hồi quy trên các tính năng hiện có."),
                        new QuestionOption("B", "Smoke Testing", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "Load Testing", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "Stress Testing", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "Đảm bảo mã mới không gây ra lỗi hồi quy trên các tính năng hiện có."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-066",
                "SE105",
                "Kỹ nghệ phần mềm & Kiến trúc",
                "Nguyên tắc Dependency Inversion Principle (DIP) khuyên chúng ta nên phụ thuộc vào cái gì?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "Phụ thuộc vào trừu tượng (Interface / Abstraction) chứ không phụ thuộc vào hiện thực cụ thể", true, "High-level modules should not depend on low-level modules; both should depend on abstractions."),
                        new QuestionOption("B", "Phụ thuộc vào lớp cơ sở dữ liệu", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "Phụ thuộc vào framework", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "Phụ thuộc vào hệ điều hành", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "High-level modules should not depend on low-level modules; both should depend on abstractions."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-067",
                "SE105",
                "Kỹ nghệ phần mềm & Kiến trúc",
                "Trong Git, lệnh nào tạo nhánh mới và chuyển ngay sang nhánh đó?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "git checkout -b <branch_name> hoặc git switch -c <branch_name>", true, "Lệnh checkout với cờ -b kết hợp tạo và chuyển nhánh trong 1 bước."),
                        new QuestionOption("B", "git branch <name>", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "git merge <name>", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "git commit -b", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "Lệnh checkout với cờ -b kết hợp tạo và chuyển nhánh trong 1 bước."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-068",
                "SE105",
                "Kỹ nghệ phần mềm & Kiến trúc",
                "Mô hình CI/CD viết tắt của hai khái niệm nào trong kỹ nghệ phần mềm?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "Continuous Integration và Continuous Delivery / Deployment", true, "Tự động hóa tích hợp, kiểm thử và chuyển giao phần mềm liên tục."),
                        new QuestionOption("B", "Code Inspection / Code Design", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "Centralized Integration", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "Computer Infrastructure", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "Tự động hóa tích hợp, kiểm thử và chuyển giao phần mềm liên tục."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-069",
                "SE105",
                "Kỹ nghệ phần mềm & Kiến trúc",
                "Khái niệm 'Technical Debt' (Nợ kỹ thuật) thể hiện điều gì?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "Chi phí và công sức phải bỏ ra sau này do chọn giải pháp dễ dãi, tạm bợ thay vì giải pháp thiết kế chuẩn mực", true, "Nợ kỹ thuật tích lũy sẽ làm chậm tốc độ phát triển và tăng nguy cơ lỗi trong tương lai."),
                        new QuestionOption("B", "Số tiền nợ bản quyền phần mềm", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "Chi phí mua máy chủ cloud", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "Lương thưởng trả chậm", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "Nợ kỹ thuật tích lũy sẽ làm chậm tốc độ phát triển và tăng nguy cơ lỗi trong tương lai."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-070",
                "SE105",
                "Kỹ nghệ phần mềm & Kiến trúc",
                "Phương pháp lập trình TDD (Test-Driven Development) tuân thủ quy trình nào?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "Red - Green - Refactor", true, "Viết test lỗi trước (Red) -> Viết mã tối thiểu để test qua (Green) -> Tối ưu mã (Refactor)."),
                        new QuestionOption("B", "Plan - Code - Test", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "Design - Build - Ship", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "Write - Run - Fix", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "Viết test lỗi trước (Red) -> Viết mã tối thiểu để test qua (Green) -> Tối ưu mã (Refactor)."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-071",
                "SE105",
                "Kỹ nghệ phần mềm & Kiến trúc",
                "Kiến trúc Hexagonal (Ports and Adapters) nhằm mục đích chính là gì?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "Cô lập logic nghiệp vụ miền (Core Domain) khỏi các công nghệ hạ tầng bên ngoài", true, "Core Domain độc lập với Framework, UI, và Database thông qua các Ports và Adapters."),
                        new QuestionOption("B", "Tăng tốc độ xử lý đồ họa", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "Tiết kiệm RAM", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "Loại bỏ hoàn toàn cơ sở dữ liệu", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "Core Domain độc lập với Framework, UI, và Database thông qua các Ports và Adapters."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-072",
                "SE105",
                "Kỹ nghệ phần mềm & Kiến trúc",
                "Chỉ số Code Coverage 80% có nghĩa là gì?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "80% số dòng lệnh trong mã nguồn đã được thực thi ít nhất một lần bởi các bộ kiểm thử", true, "Đo lường mức độ bao phủ mã của các bài kiểm thử tự động."),
                        new QuestionOption("B", "80% mã nguồn không có lỗi", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "80% mã đã được viết xong", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "80% chức năng được giao hàng", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "Đo lường mức độ bao phủ mã của các bài kiểm thử tự động."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-073",
                "SE105",
                "Kỹ nghệ phần mềm & Kiến trúc",
                "Mẫu thiết kế nào cho phép thêm trách nhiệm mới cho đối tượng một cách linh hoạt trong runtime mà không dùng kế thừa?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "Decorator Pattern", true, "Decorator bọc đối tượng gốc và bổ sung thêm hành vi."),
                        new QuestionOption("B", "Singleton Pattern", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "Factory Method", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "Composite Pattern", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "Decorator bọc đối tượng gốc và bổ sung thêm hành vi."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-074",
                "SE105",
                "Kỹ nghệ phần mềm & Kiến trúc",
                "Phân biệt giữa Authentication và Authorization là gì?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "Authentication xác định bạn là ai, Authorization xác định bạn có quyền làm gì", true, "Xác thực danh tính (Authentication) và Phân quyền truy cập tài nguyên (Authorization)."),
                        new QuestionOption("B", "Authentication kiểm tra mật khẩu, Authorization kiểm tra tài khoản", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "Hai khái niệm hoàn toàn tương đương", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "Authentication chỉ dùng cho admin", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "Xác thực danh tính (Authentication) và Phân quyền truy cập tài nguyên (Authorization)."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-075",
                "SE105",
                "Kỹ nghệ phần mềm & Kiến trúc",
                "Clean Architecture của Robert C. Martin quy định quy tắc phụ thuộc (Dependency Rule) như thế nào?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "Các phụ thuộc mã nguồn chỉ được phép trỏ từ ngoài vào trong hướng về Core Business Entities", true, "Tầng bên trong không hề biết gì về tầng bên ngoài."),
                        new QuestionOption("B", "Trỏ tự do hai chiều", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "Phụ thuộc từ trong ra ngoài", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "Tất cả phụ thuộc vào Database", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "Tầng bên trong không hề biết gì về tầng bên ngoài."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-076",
                "SEC106",
                "An toàn thông tin & Mật mã",
                "Thuật toán mã hóa nào sau đây là thuật toán mã hóa đối xứng (Symmetric Encryption)?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "AES (Advanced Encryption Standard)", true, "AES dùng chung một khóa bí mật cho cả mã hóa và giải mã."),
                        new QuestionOption("B", "RSA", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "ECC", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "Diffie-Hellman", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "AES dùng chung một khóa bí mật cho cả mã hóa và giải mã."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-077",
                "SEC106",
                "An toàn thông tin & Mật mã",
                "Độ dài băm đầu ra chuẩn của thuật toán SHA-256 là bao nhiêu bit?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "256 bits (32 bytes)", true, "SHA-256 luôn tạo chuỗi băm cố định 256 bits (64 ký tự hex)."),
                        new QuestionOption("B", "128 bits", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "512 bits", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "64 bits", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "SHA-256 luôn tạo chuỗi băm cố định 256 bits (64 ký tự hex)."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-078",
                "SEC106",
                "An toàn thông tin & Mật mã",
                "Lỗ hổng bảo mật SQL Injection cho phép kẻ tấn công thực hiện hành vi nguy hiểm nào?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "Chèn các câu lệnh SQL độc hại để truy cập hoặc thao túng dữ liệu trái phép", true, "Xảy ra khi dữ liệu đầu vào của người dùng được ghép chuỗi trực tiếp vào truy vấn SQL."),
                        new QuestionOption("B", "Làm tràn bộ đệm RAM", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "Tấn công từ chối dịch vụ mạng", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "Đọc trộm màn hình", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "Xảy ra khi dữ liệu đầu vào của người dùng được ghép chuỗi trực tiếp vào truy vấn SQL."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-079",
                "SEC106",
                "An toàn thông tin & Mật mã",
                "Cơ chế nào được dùng để phòng chống tấn công CSRF (Cross-Site Request Forgery)?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "Sử dụng Anti-CSRF Token ngẫu nhiên kết hợp cờ SameSite cho Cookies", true, "Token bí mật chỉ client hợp lệ mới có thể gửi kèm trong form."),
                        new QuestionOption("B", "Mã hóa mật khẩu MD5", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "Dùng HTTPS", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "Tăng kích thước Session", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "Token bí mật chỉ client hợp lệ mới có thể gửi kèm trong form."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-080",
                "SEC106",
                "An toàn thông tin & Mật mã",
                "Lỗ hổng XSS (Cross-Site Scripting) xảy ra khi nào?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "Mã JavaScript độc hại được tiêm vào trình duyệt của người dùng khác do thiếu lọc đầu ra", true, "Kẻ tấn công đánh cắp cookie session hoặc giả mạo giao diện người dùng."),
                        new QuestionOption("B", "SQL server bị tấn công", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "Server bị quá tải CPU", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "Dây cáp mạng bị đứt", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "Kẻ tấn công đánh cắp cookie session hoặc giả mạo giao diện người dùng."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-081",
                "SEC106",
                "An toàn thông tin & Mật mã",
                "Giao thức trao đổi khóa Diffie-Hellman cho phép hai bên làm gì?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "Thỏa thuận một khóa bí mật chung qua một kênh truyền thông không an toàn", true, "Tạo khóa bí mật chung mà kẻ nghe lén trên đường truyền không thể tính toán được."),
                        new QuestionOption("B", "Ký số văn bản", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "Băm mật khẩu", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "Mã hóa email", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "Tạo khóa bí mật chung mà kẻ nghe lén trên đường truyền không thể tính toán được."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-082",
                "SEC106",
                "An toàn thông tin & Mật mã",
                "Muối (Salt) trong quá trình băm mật khẩu có tác dụng chính là gì?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "Vô hiệu hóa các bảng băm tính sẵn (Rainbow Tables) và ngăn hai mật khẩu giống nhau có cùng giá trị băm", true, "Salt là chuỗi ngẫu nhiên được ghép vào mật khẩu trước khi đưa qua hàm băm BCrypt/Argon2."),
                        new QuestionOption("B", "Tăng tốc độ băm mật khẩu", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "Nén dung lượng băm", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "Cho phép giải mã mật khẩu", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "Salt là chuỗi ngẫu nhiên được ghép vào mật khẩu trước khi đưa qua hàm băm BCrypt/Argon2."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-083",
                "SEC106",
                "An toàn thông tin & Mật mã",
                "Chữ ký số (Digital Signature) sử dụng khóa nào để tạo chữ ký?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "Khóa riêng (Private Key) của người gửi", true, "Người gửi ký bằng Private Key, bất kỳ ai cũng có thể xác minh bằng Public Key tương ứng."),
                        new QuestionOption("B", "Khóa công khai của người gửi", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "Khóa công khai của người nhận", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "Khóa đối xứng", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "Người gửi ký bằng Private Key, bất kỳ ai cũng có thể xác minh bằng Public Key tương ứng."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-084",
                "SEC106",
                "An toàn thông tin & Mật mã",
                "Nguyên tắc Least Privilege (Đặc quyền tối thiểu) trong bảo mật yêu cầu điều gì?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "Chỉ cấp cho người dùng hoặc tiến trình các quyền hạn tối thiểu cần thiết để thực hiện công việc", true, "Giảm thiểu bề mặt tấn công và thiệt hại khi tài khoản bị chiếm đoạt."),
                        new QuestionOption("B", "Tất cả người dùng đều có quyền Root", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "Chỉ Admin mới được dùng hệ thống", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "Cấp toàn quyền để tiện làm việc", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "Giảm thiểu bề mặt tấn công và thiệt hại khi tài khoản bị chiếm đoạt."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-085",
                "SEC106",
                "An toàn thông tin & Mật mã",
                "Tấn công Man-in-the-Middle (MITM) có thể bị ngăn chặn hiệu quả nhất bằng cách nào?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "Sử dụng HTTPS với chứng chỉ số X.509 được cấp phát bởi Certificate Authority (CA) tin cậy", true, "Chứng chỉ số xác thực danh tính máy chủ và mã hóa kênh truyền TLS."),
                        new QuestionOption("B", "Tắt tường lửa", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "Đổi mật khẩu định kỳ", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "Dùng cổng 8080", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "Chứng chỉ số xác thực danh tính máy chủ và mã hóa kênh truyền TLS."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-086",
                "SEC106",
                "An toàn thông tin & Mật mã",
                "Đặc điểm nào là điểm yếu lớn nhất của thuật toán mã hóa DES khiến nó bị thay thế?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "Không gian khóa quá nhỏ (chỉ 56 bits), dễ bị bẻ gãy bằng tấn công vét cạn (Brute-force)", true, "Khóa 56-bit có thể bị máy tính chuyên dụng bẻ gãy trong vòng vài giờ."),
                        new QuestionOption("B", "Tốc độ tính toán quá chậm", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "Không hỗ trợ phần cứng", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "Thuật toán không công khai", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "Khóa 56-bit có thể bị máy tính chuyên dụng bẻ gãy trong vòng vài giờ."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-087",
                "SEC106",
                "An toàn thông tin & Mật mã",
                "Cờ 'HttpOnly' trên Cookie có tác dụng bảo vệ gì?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "Ngăn chặn mã JavaScript trên trình duyệt truy cập vào Cookie, phòng chống đánh cắp qua XSS", true, "document.cookie không thể đọc được các cookie có cờ HttpOnly."),
                        new QuestionOption("B", "Tự động xóa Cookie khi tắt máy", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "Chỉ gửi Cookie qua HTTPS", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "Không cho Cookie hết hạn", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "document.cookie không thể đọc được các cookie có cờ HttpOnly."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-088",
                "SEC106",
                "An toàn thông tin & Mật mã",
                "Cờ 'Secure' trên Cookie đảm bảo điều gì?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "Cookie chỉ được trình duyệt gửi qua các kết nối được mã hóa an toàn (HTTPS)", true, "Ngăn chặn việc lộ Cookie qua kênh truyền HTTP không mã hóa."),
                        new QuestionOption("B", "Cookie được mã hóa trong ổ cứng", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "Chỉ Admin mới đọc được", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "Không cho sao chép", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "Ngăn chặn việc lộ Cookie qua kênh truyền HTTP không mã hóa."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-089",
                "SEC106",
                "An toàn thông tin & Mật mã",
                "Cơ chế xác thực hai yếu tố (2FA - TOTP) hoạt động dựa trên yếu tố nào?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "Thời gian thực hiện tại và một khóa bí mật chia sẻ chung (HMAC-SHA1)", true, "Time-based One-Time Password sinh mã 6 chữ số thay đổi mỗi 30 giây."),
                        new QuestionOption("B", "Vân tay của người dùng", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "Địa chỉ IP tĩnh", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "Số điện thoại gửi SMS", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "Time-based One-Time Password sinh mã 6 chữ số thay đổi mỗi 30 giây."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-090",
                "SEC106",
                "An toàn thông tin & Mật mã",
                "Chính sách CORS (Cross-Origin Resource Sharing) được thực thi bởi thành phần nào?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "Trình duyệt web (Web Browser)", true, "Trình duyệt chặn các yêu cầu HTTP fetch từ origin khác nếu server không trả header CORS hợp lệ."),
                        new QuestionOption("B", "Máy chủ Web Server", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "Router mạng", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "Database Server", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "Trình duyệt chặn các yêu cầu HTTP fetch từ origin khác nếu server không trả header CORS hợp lệ."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-091",
                "SYS108",
                "Hệ thống phân tán & Đám mây",
                "Định lý CAP phát biểu rằng hệ thống phân tán không thể đồng thời đảm bảo 3 yếu tố nào?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "Consistency (Nhất quán), Availability (Sẵn sàng), Partition Tolerance (Chịu phân vùng)", true, "Khi mạng bị phân vùng, bắt buộc phải chọn giữa Tính nhất quán hoặc Tính sẵn sàng."),
                        new QuestionOption("B", "Concurrency, Accuracy, Performance", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "Compute, Access, Protection", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "Cache, API, Protocol", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "Khi mạng bị phân vùng, bắt buộc phải chọn giữa Tính nhất quán hoặc Tính sẵn sàng."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-092",
                "SYS108",
                "Hệ thống phân tán & Đám mây",
                "Thuật toán đồng thuận Raft đạt được sự đồng thuận phân tán thông qua cơ chế nào?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "Bầu chọn Leader duy nhất và Leader chịu trách nhiệm sao chép nhật ký (Log Replication)", true, "Raft phân rã bài toán đồng thuận thành Bầu chọn Leader, Sao chép Log và Đảm bảo an toàn."),
                        new QuestionOption("B", "Bỏ phiếu ngang hàng p2p", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "Khóa hai pha tập trung", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "Token ring", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "Raft phân rã bài toán đồng thuận thành Bầu chọn Leader, Sao chép Log và Đảm bảo an toàn."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-093",
                "SYS108",
                "Hệ thống phân tán & Đám mây",
                "Hàng đợi thông điệp Apache Kafka đảm bảo thứ tự của thông điệp ở cấp độ nào?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "Trong cùng một Partition", true, "Thông điệp trong cùng 1 partition được ghi tuần tự và đảm bảo thứ tự tuyệt đối theo offset."),
                        new QuestionOption("B", "Trên toàn bộ Topic", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "Trên toàn cụm Cluster", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "Không đảm bảo thứ tự", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "Thông điệp trong cùng 1 partition được ghi tuần tự và đảm bảo thứ tự tuyệt đối theo offset."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-094",
                "SYS108",
                "Hệ thống phân tán & Đám mây",
                "Khái niệm 'Idempotency' (Tính lũy đẳng) của một API có nghĩa là gì?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "Thực hiện gọi API nhiều lần với cùng tham số mang lại kết quả và trạng thái giống hệt như gọi 1 lần", true, "DELETE hoặc PUT thường là idempotent, giúp retry an toàn khi mạng chập chờn."),
                        new QuestionOption("B", "API chạy cực nhanh", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "API không trả về lỗi", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "API chỉ gọi được một lần", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "DELETE hoặc PUT thường là idempotent, giúp retry an toàn khi mạng chập chờn."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-095",
                "SYS108",
                "Hệ thống phân tán & Đám mây",
                "Giao thức gRPC sử dụng định dạng tuần tự hóa dữ liệu nào làm mặc định?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "Protocol Buffers (Protobuf nhị phân)", true, "Protobuf đóng gói dữ liệu dạng nhị phân cực kỳ nhỏ gọn và giải mã siêu tốc."),
                        new QuestionOption("B", "JSON", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "XML", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "YAML", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "Protobuf đóng gói dữ liệu dạng nhị phân cực kỳ nhỏ gọn và giải mã siêu tốc."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-096",
                "SYS108",
                "Hệ thống phân tán & Đám mây",
                "Mô hình xử lý dữ liệu MapReduce bao gồm hai pha chính nào?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "Pha Map (chuyển đổi/phát xạ) và pha Reduce (tổng hợp/gom nhóm)", true, "Pha Map ánh xạ từng phần tử thành key-value, Pha Reduce tổng hợp tất cả value theo từng key."),
                        new QuestionOption("B", "Pha Filter và pha Sort", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "Pha Read và pha Write", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "Pha Split và pha Join", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "Pha Map ánh xạ từng phần tử thành key-value, Pha Reduce tổng hợp tất cả value theo từng key."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-097",
                "SYS108",
                "Hệ thống phân tán & Đám mây",
                "Cơ chế Service Discovery trong kiến trúc Microservices giải quyết bài toán gì?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "Tự động phát hiện địa chỉ IP và cổng mạng của các phiên bản dịch vụ linh động", true, "Cho phép các microservice tìm thấy nhau mà không cần cấu hình IP cứng tĩnh."),
                        new QuestionOption("B", "Tìm kiếm code lỗi", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "Quét virus mạng", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "Tự động sinh tài liệu Swagger", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "Cho phép các microservice tìm thấy nhau mà không cần cấu hình IP cứng tĩnh."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-098",
                "SYS108",
                "Hệ thống phân tán & Đám mây",
                "Kỹ thuật Sharding nhất quán (Consistent Hashing) giúp giải quyết vấn đề gì khi thêm/bớt node?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "Chỉ một tỉ lệ nhỏ khóa K/N cần phải di chuyển vị trí lưu trữ", true, "Tránh hiện tượng bão phân phối lại dữ liệu (Cache Stampede / Rehash Toàn bộ)."),
                        new QuestionOption("B", "Tất cả dữ liệu phải băm lại", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "Loại bỏ hoàn toàn lỗi", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "Tăng gấp đôi RAM", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "Tránh hiện tượng bão phân phối lại dữ liệu (Cache Stampede / Rehash Toàn bộ)."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-099",
                "SYS108",
                "Hệ thống phân tán & Đám mây",
                "Khái niệm 'Split-Brain' trong cụm phân tán đề cập đến hiện tượng gì?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "Cụm bị chia cắt thành 2 nửa và mỗi nửa đều tự bầu chọn một Leader riêng gây xung đột dữ liệu", true, "Được ngăn chặn bằng quy tắc túc số Quorum (N/2 + 1 node đồng thuận)."),
                        new QuestionOption("B", "CPU bị lỗi phần cứng", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "Hệ điều hành bị treo", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "Bộ nhớ bị phân mảnh", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "Được ngăn chặn bằng quy tắc túc số Quorum (N/2 + 1 node đồng thuận)."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-100",
                "SYS108",
                "Hệ thống phân tán & Đám mây",
                "Trình điều phối container Kubernetes sử dụng đơn vị triển khai nhỏ nhất nào?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "Pod", true, "Pod chứa một hoặc nhiều container chia sẻ chung mạng và khối lưu trữ."),
                        new QuestionOption("B", "Container", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "Deployment", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "Service", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "Pod chứa một hoặc nhiều container chia sẻ chung mạng và khối lưu trữ."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-101",
                "SYS108",
                "Hệ thống phân tán & Đám mây",
                "Kỹ thuật 'Circuit Breaker' giúp bảo vệ hệ thống phân tán khỏi hiện tượng nào?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "Cascading Failure (Lỗi sụp đổ dây chuyền giữa các dịch vụ)", true, "Ngắt kết nối tới dịch vụ đang hỏng để hệ thống không bị cạn kiệt tài nguyên chờ đợi."),
                        new QuestionOption("B", "Tấn công DDoS", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "Tràn bộ nhớ JVM", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "Mất kết nối Internet", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "Ngắt kết nối tới dịch vụ đang hỏng để hệ thống không bị cạn kiệt tài nguyên chờ đợi."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-102",
                "SYS108",
                "Hệ thống phân tán & Đám mây",
                "Mô hình Eventual Consistency (Nhất quán cuối cùng) chấp nhận điều gì?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "Trạng thái các nút có thể tạm thời không đồng nhất nhưng sẽ nhất quán sau một khoảng thời gian", true, "Đổi tính tức thời lấy hiệu năng cao và tính sẵn sàng vượt trội theo nguyên lý BASE."),
                        new QuestionOption("B", "Dữ liệu có thể bị mất mãi mãi", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "Hệ thống không cần lưu trữ", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "Dữ liệu luôn sai", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "Đổi tính tức thời lấy hiệu năng cao và tính sẵn sàng vượt trội theo nguyên lý BASE."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-103",
                "SYS108",
                "Hệ thống phân tán & Đám mây",
                "Mục đích chính của công cụ điều phối phân tán Apache ZooKeeper hoặc etcd là gì?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "Lưu trữ cấu hình phân tán, quản lý đồng bộ và bầu chọn trưởng nhóm (Leader Election)", true, "Cung cấp kho lưu trữ key-value phân tán nhất quán cao (CP) phục vụ điều phối hệ thống."),
                        new QuestionOption("B", "Lưu video dung lượng lớn", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "Chạy thuật toán học máy", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "Tăng tốc mạng LAN", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "Cung cấp kho lưu trữ key-value phân tán nhất quán cao (CP) phục vụ điều phối hệ thống."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-104",
                "SYS108",
                "Hệ thống phân tán & Đám mây",
                "Giao thức 2-Phase Commit (2PC) có nhược điểm lớn nhất là gì?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "Là giao thức khóa chặn (Blocking Protocol), dễ gây treo toàn bộ hệ thống nếu Coordinator bị sập", true, "Tất cả các bên tham gia phải giữ khóa tài nguyên trong suốt thời gian chờ lệnh Commit."),
                        new QuestionOption("B", "Dữ liệu không nhất quán", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "Không hỗ trợ cơ sở dữ liệu quan hệ", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "Khó viết mã", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "Tất cả các bên tham gia phải giữ khóa tài nguyên trong suốt thời gian chờ lệnh Commit."
        ));

        registerQuestion(new QuizQuestionItem(
                "Q-105",
                "SYS108",
                "Hệ thống phân tán & Đám mây",
                "Kỹ thuật 'Rate Limiting' Token Bucket cho phép điều gì mà Leaky Bucket không hỗ trợ?",
                "MULTIPLE_CHOICE",
                "MEDIUM",
                List.of(
                        new QuestionOption("A", "Cho phép đáp ứng các đợt lưu lượng tăng đột biến (Burst Traffic) trong giới hạn số lượng token", true, "Khi hòm token còn đầy, các request gửi đến dồn dập vẫn được xử lý ngay lập tức."),
                        new QuestionOption("B", "Không cho phép vượt ngưỡng", false, "Lựa chọn này không chính xác trong ngữ cảnh câu hỏi."),
                        new QuestionOption("C", "Tốc độ luôn cố định", false, "Khái niệm này áp dụng cho trường hợp hoặc tầng giao thức khác."),
                        new QuestionOption("D", "Xóa toàn bộ request", false, "Đây là định nghĩa không phù hợp với yêu cầu bài toán.")
                ),
                "Khi hòm token còn đầy, các request gửi đến dồn dập vẫn được xử lý ngay lập tức."
        ));

    }

    private static void registerQuestion(QuizQuestionItem q) {
        QUESTIONS.put(q.id(), q);
    }

    public static List<QuizQuestionItem> getAllQuestions() {
        return List.copyOf(QUESTIONS.values());
    }

    public static Optional<QuizQuestionItem> findById(String id) {
        return Optional.ofNullable(QUESTIONS.get(id));
    }

    public static List<QuizQuestionItem> findBySubject(String subjectCode) {
        return QUESTIONS.values().stream()
                .filter(q -> q.subjectCode().equalsIgnoreCase(subjectCode))
                .toList();
    }

    public static List<QuizQuestionItem> findByTopic(String topic) {
        return QUESTIONS.values().stream()
                .filter(q -> q.topic().equalsIgnoreCase(topic))
                .toList();
    }

    public static List<QuizQuestionItem> findByDifficulty(String difficulty) {
        if (difficulty == null || difficulty.isBlank()) {
            return getAllQuestions();
        }
        return QUESTIONS.values().stream()
                .filter(q -> q.difficulty().equalsIgnoreCase(difficulty))
                .toList();
    }

    public static List<QuizQuestionItem> search(String query) {
        if (query == null || query.isBlank()) {
            return getAllQuestions();
        }
        String q = query.toLowerCase();
        return QUESTIONS.values().stream()
                .filter(x -> x.questionText().toLowerCase().contains(q) || x.topic().toLowerCase().contains(q))
                .toList();
    }

    public static int getTotalQuestionCount() {
        return QUESTIONS.size();
    }
}
