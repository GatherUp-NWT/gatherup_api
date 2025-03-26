
package org.app.invitationservice.response;

import java.time.LocalDateTime;
import org.app.invitationservice.entity.UserInvite;

public record InvitationModel(
    String sendByUserName,
    String eventName,
    LocalDateTime sendDate,
    String responseType
) {
}
