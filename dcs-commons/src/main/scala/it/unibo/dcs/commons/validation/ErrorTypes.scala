package it.unibo.dcs.commons.validation

object ErrorTypes {

  val userServiceWrongResponse = "USER_SERVICE_WRONG_RESPONSE"

  val createRoomError = "ROOM_CREATION_ERROR"
  val deleteRoomError = "ROOM_DELETION_ERROR"
  val createUserError = "USER_CREATION_ERROR"

  val missingResponseBody = "MISSING_RESPONSE_BODY"
  val missingRequestBody = "MISSING_REQUEST_BODY"
  val missingUsername = "MISSING_USERNAME"
  val missingFirstName = "MISSING_FIRST_NAME"
  val missingLastName = "MISSING_LAST_NAME"
  val missingCredentials = "MISSING_CREDENTIALS"

  val usernameAlreadyTaken = "USERNAME_ALREADY_TAKEN"

  val userNotFound = "USER_NOT_FOUND"

  val invalidToken = "INVALID_TOKEN"

  val unexpectedLogoutError = "UNEXPECTED_LOGOUT_ERROR"
  val unexpectedTokenError = "UNEXPECTED_TOKEN_ERROR"
}
