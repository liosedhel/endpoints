package endpoints.algebra

import endpoints._

import scala.language.higherKinds

trait Requests extends Urls with Methods with InvariantFunctorSyntax with SemigroupalSyntax {

  /** Information carried by requests’ headers */
  type RequestHeaders[A]

  /**
    * No particular information. Does not mean that the headers *have to*
    * be empty. Just that, from a server point of view no information will
    * be extracted from them, and from a client point of view no particular
    * headers will be built in the request.
    */
  def emptyHeaders: RequestHeaders[Unit]

  def header(name: String, docs: Documentation = None): RequestHeaders[String]

  def optHeader(name: String, docs: Documentation = None): RequestHeaders[Option[String]]

  implicit def reqHeadersSemigroupal: Semigroupal[RequestHeaders]
  implicit def reqHeadersInvFunctor: InvariantFunctor[RequestHeaders]


  /** Information carried by a whole request (headers and entity) */
  type Request[A]

  /** Information carried by request entity */
  type RequestEntity[A]

  implicit def reqEntityInvFunctor: InvariantFunctor[RequestEntity]

  /**
    * Empty request.
    */
  //TODO rename to emptyBody and maybe make uniform with all other by adding ()
  def emptyRequest: RequestEntity[Unit]

  /**
    * Request with string body.
    */
  def textRequest(docs: Documentation = None): RequestEntity[String]

  /**
    * Request for given parameters
    *
    * @param method Request method
    * @param url Request URL
    * @param entity Request entity
    * @param headers Request headers
    * @tparam UrlP Payload carried by url
    * @tparam BodyP Payload carried by body
    * @tparam HeadersP Payload carried by headers
    * @tparam UrlAndBodyPTupled Payloads of Url and Body tupled together by [[Tupler]]
    */
  def request[UrlP, BodyP, HeadersP, UrlAndBodyPTupled](
    method: Method,
    url: Url[UrlP],
    entity: RequestEntity[BodyP] = emptyRequest,
    headers: RequestHeaders[HeadersP] = emptyHeaders
  )(implicit tuplerAB: Tupler.Aux[UrlP, BodyP, UrlAndBodyPTupled], tuplerABC: Tupler[UrlAndBodyPTupled, HeadersP]): Request[tuplerABC.Out]

  /**
    * Helper method to perform GET request
    * @tparam UrlP Payload carried by url
    * @tparam HeadersP Payload carried by headers
    */
  final def get[UrlP, HeadersP](
    url: Url[UrlP],
    headers: RequestHeaders[HeadersP] = emptyHeaders
  )(implicit tuplerAC: Tupler[UrlP, HeadersP]): Request[tuplerAC.Out] = request(Get, url, headers = headers)

  /**
    * Helper method to perform POST request
    * @tparam UrlP Payload carried by url
    * @tparam BodyP Payload carried by body
    * @tparam HeadersP Payload carried by headers
    * @tparam UrlAndBodyPTupled Payloads of Url and Body tupled together by [[Tupler]]
    */
  final def post[UrlP, BodyP, HeadersP, UrlAndBodyPTupled](
    url: Url[UrlP],
    entity: RequestEntity[BodyP],
    headers: RequestHeaders[HeadersP] = emptyHeaders
  )(implicit tuplerAB: Tupler.Aux[UrlP, BodyP, UrlAndBodyPTupled], tuplerABC: Tupler[UrlAndBodyPTupled, HeadersP]): Request[tuplerABC.Out] = request(Post, url, entity, headers)


}
