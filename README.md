# java-filmorate

https://dbdiagram.io/d/6465fc09dca9fb07c45180a0

Приложение для поиска фильмов, обмена понравившихся

Добавление в друзья

* Контроллер PUT, отвечающий за добавделение в друзья.
*
* @param id - передается по http в заголовке запроса.
* @param friendId - передается по http в заголовке запроса.
* @return возвращает код ответа с уже записанной в бд сущностью.
  */
  @PutMapping("/{id}/friends/{friendId}")
  ResponseEntity<User> addToFriends(@PathVariable long id, @PathVariable long friendId) {
  log.info("Request user for add to friend {}", friendId);
  return ResponseEntity.status(HttpStatus.OK).body(userService.addForFriends(id, friendId));
  }

Добавление лайков

* Контроллер PUT, отвечающий за постановления лайка.
*
* @param id - передается по http в заголовке запроса.
* @param userId - передается по http в заголовке запроса.
* @return возвращает фильм с поставленым лайком.
  */
  @PutMapping("/{id}/like/{userId}")
  public ResponseEntity<Film> likeInFilm(@PathVariable long id, @PathVariable long userId) {
  return ResponseEntity.status(HttpStatus.OK).body(filmService.addLikesInFIlm(id, userId));
  }


* Контроллер GET, отвечающий за получения списка друзей.
* 
* @param id - передается по http в заголовке запроса.
* @return возвращает список дрйзей пользователя.
*/
@GetMapping("/{id}/friends")
ResponseEntity<List<User>> getListFriends(@PathVariable long id) {
log.info("Request user for get to list friend {}", id);
return ResponseEntity.status(HttpStatus.OK).body(userService.listFriends(id));
  }

