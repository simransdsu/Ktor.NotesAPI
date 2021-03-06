package dev.simran.routes

import dev.simran.data.checkIfUserExists
import dev.simran.data.checkPasswordForEmail
import dev.simran.data.collections.User
import dev.simran.data.registerUser
import dev.simran.data.requests.AccountRequest
import dev.simran.data.responses.SimpleResponse
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.registerRoute() {
    /**
     * Register a user with email and password
     */
    route("/register") {
        post {
            val request = try {
                call.receive<AccountRequest>()
            } catch (e: ContentTransformationException) {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            val userExists = checkIfUserExists(request.email)

            if(!userExists) {
                val user = User(request.email.toLowerCase(), request.password)
                if(registerUser(user)) {
                    call.respond(HttpStatusCode.OK, SimpleResponse("Successfully registered user.", true))
                } else {
                    call.respond(HttpStatusCode.ExpectationFailed, SimpleResponse("User registration failed", true))
                    return@post
                }
            } else {
                call.respond(HttpStatusCode.OK, SimpleResponse("A user with that email already exists.", false))
            }
        }
    }

    /**
     * Login a user with valid email and password
     */
    route("/login") {
        post {
            val request = try {
                call.receive<AccountRequest>()
            } catch (e: ContentTransformationException) {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            val isPasswordCorrect = checkPasswordForEmail(
                email = request.email.toLowerCase(),
                passwordToCheck = request.password
            )

            if(isPasswordCorrect) {
                call.respond(HttpStatusCode.OK, SimpleResponse(successful = true, message = "You are now logged in"))
            } else {
                call.respond(HttpStatusCode.Unauthorized, SimpleResponse(successful = false, message = "Email/Password is incorrect"))
            }
        }
    }
}