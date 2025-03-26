
package org.app.invitationservice.exceptions;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
  private LocalDateTime timestamp;
  private int status;
  private String message;
  private String path;
}
