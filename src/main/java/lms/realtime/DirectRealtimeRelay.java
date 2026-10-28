package lms.realtime;

import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Component;

import lms.course.CourseService;

/** Server web: gửi thẳng qua RealtimeHub sau khi commit (không có transaction thì gửi ngay). */
@Component
public class DirectRealtimeRelay implements RealtimeRelay {

    private final RealtimeHub hub;

    public DirectRealtimeRelay(RealtimeHub hub) {
        this.hub = hub;
    }

    @Override
    public void sendAfterCommit(Collection<Long> userIds, String type, Object data) {
        List<Long> targets = List.copyOf(userIds);
        RealtimeEvent event = RealtimeEvent.of(type, data);
        CourseService.afterCommit(() -> targets.forEach(u -> hub.sendToUser(u, event)));
    }
}
