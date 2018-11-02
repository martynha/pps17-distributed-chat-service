package it.unibo.dcs.service.webapp.usecases

import java.util.Date

import it.unibo.dcs.service.webapp.interaction.Requests.{CheckTokenRequest, SendMessageRequest}
import it.unibo.dcs.service.webapp.interaction.Results.SendMessageResult
import it.unibo.dcs.service.webapp.model.{Message, Room}
import it.unibo.dcs.service.webapp.repositories.RoomRepository
import rx.lang.scala.{Observable, Subscriber}

import scala.language.postfixOps

class SendMessageUseCaseSpec extends UseCaseSpec {
  private val roomRepository: RoomRepository = mock[RoomRepository]

  private val room = Room("Room 1")
  private val content = "Message content"
  private val timestamp = new Date
  private val message = Message(room, user.username, content, timestamp)

  private val checkTokenRequest = CheckTokenRequest(token, user.username)
  private val sendMessageRequest = SendMessageRequest(room.name, user.username, content, timestamp, token)

  private val sendMessageResult = SendMessageResult(message)
  private val sendMessageSubscriber = stub[Subscriber[SendMessageResult]]

  private val sendMessageUseCase =
    new SendMessageUseCase(threadExecutor, postExecutionThread, authRepository, roomRepository)

  it should "execute the send message use case" in {
    // Given
    (authRepository checkToken _) expects checkTokenRequest returns (Observable just token)
    (roomRepository sendMessage _) expects sendMessageRequest returns (Observable just message)

    // When
    // sendMessageUseCase is executed with argument `request`
    sendMessageUseCase(sendMessageRequest) subscribe sendMessageSubscriber

    // Then
    (sendMessageSubscriber onNext _) verify sendMessageResult once()
    (() => sendMessageSubscriber onCompleted) verify() once()
  }
}
