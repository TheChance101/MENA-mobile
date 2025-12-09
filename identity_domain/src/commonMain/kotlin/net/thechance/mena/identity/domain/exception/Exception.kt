package net.thechance.mena.identity.domain.exception

abstract class AuthenticationException(message: String) : Exception(message)
abstract class LocationException(message: String) : Exception(message)

class InvalidCountryCodeException(countryCode: String) : AuthenticationException("country code: $countryCode is not valid or not supported yet")

class InvalidMobileNumberException(
    mobileNumber: String
) : AuthenticationException("mobile number: $mobileNumber doesn't match validation")

class UserIsBlockedException : AuthenticationException("user with mobile number: has many login retries")
class InvalidPasswordException : AuthenticationException("password doesn't match validations")

class InvalidCredentialsException : AuthenticationException(
    "user with mobile number, doesn't exist or password is incorrect"
)
class UnknownException : AuthenticationException("Unknown Exception")
class UnAuthorizedException : AuthenticationException("user has no access to application")
class PhoneNumberAlreadyExistsException : AuthenticationException("Phone number already exists")
class UsernameAlreadyExistsException : AuthenticationException("Username already exists")
class TooManyRequestsException : AuthenticationException("Too many requests")
class NoNetworkException : AuthenticationException("No Internet Connection")
class InvalidRequestException() : AuthenticationException("Invalid request")
class UnableToFindLocationException() : LocationException("Unable to find location")
class CannotOpenSettingsException() : LocationException("Cannot open settings")
class AddressNotFoundException() : LocationException("Address not found")
