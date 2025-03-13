## TankiRating 2.4.x

### RU
* [2.4.0] Восстановлена работа портала
* [2.4.1] Premium статус для донаторов
* [2.4.2] Включена статистика по режимам. Архивные данные будут восстановлены позже
* [2.4.2] Кэширование рейтинга на главной странице (обновляется раз в 15 минут)

### EN
* [2.4.0] Restore portal work
* [2.4.1] Premium status for donators
* [2.4.2] Enable mode stats. Old data will be restored later
* [2.4.2] Rating on the main page will be refreshed every 15 minutes

## TankiRating 2.3.x

### RU

* [2.3.2] Фикс бага в entity service (phantom save)
* [2.3.1] Альтернатива вернула статистику по модулям, приняты меры чтобы танкирейтинги не сошли с ума
* [2.3.0] Возможность помечать аккаунты как "избранные", отдельный фильтр на странице рейтинга по любимым аккаунтам. Избранные аккаунты хранятся локально на устройстве. 
* [2.3.0] Пофикшен трекинг премиума (раньше была ситуация, когда считало на один день больше, чем было на самом деле, и в неделе получалось 8 дней према к примеру)
* [2.3.0] DISABLED аккаунты теперь будут обновляться раз в неделю, на случай если они снова станут активными

### EN

* [2.3.1] Alternativa brought back module statistics, added workarounds to work with this shitty platform
* [2.3.0] Introduce an ability to add favourite accounts. Add radio button to show only favourite accounts in the main ratings page
* [2.3.0] Fix bug with premium tracking (premium percent > 100)
* [2.3.0] DISABLED accounts now updates ones per week, in a case if they became available

## TankiRating 2.2.x

### RU

* (2.2.3) Поддерживать корректную статистику по модулям за проблемные периоды (которые включают в себя обнуление) очень затратно, система не справляется. Поэтому за проблемные периоды статистика будет не полной. Считайте, что она считается с момента обнуления заново. 
* (2.2.2) Попытка фикса отрицательной статистики у модулей, спасибо альтернативе за слом счетчика
* (2.2.1) Изменен график обновления. Включены частые обновления FROZEN аккаунтов
* Пофикшен баг с трекингом дневной активности (раньше могли пропасть данные за последние 3 часа игрового дня)
* Добавлена кнопочка чтобы вручную обновить аккаунт
* Информация о версии в шапке страницы, плюс ссылка на чейнжлоги
* Сортировка на главной по-умолчанию киллы/время, а не по уп, кемперы сосатб
* Время битвы выставлено в 13мин, параметры показываются относительно него

### EN

* (2.2.3) Module stat calculation for problem periods led to performance problems. So now you may see incomplete module stat. 
* (2.2.2) Attempt to fix negative module stat, thanks to alternativa broken ratings
* (2.2.1) Change update config. Enable frozen account frequently updates
* Fixed bug with daily diff tracking (last 3 hours in the day not counted)
* Add ability to manually update user (just click on update button in the user page)
* Add version information to the navigation bar
* Change default sorting in the home page from k/d to k/t. 
* Adjust parameters to fill 12min battle time

#### Misc

* Swagger api is working now
* Remove unused data from database, fix bug with snapshot sanitizer


## TankiRating 2.1.x

### RU

* Возможность вручную включать отключенные системой аккаунты
* Информация о общей сумме наигранных часов в рейтинге легенд
* Новый статус аккаунта - спящий (SLEEP). Если на аккаунте больше 3 дней нет активности, то он становится спящим и обновляется раз в день.
Активные же аккаунты обновляются по-прежнему раз в 3 часа (кроме ночного времени)

### EN

* Introduce the ability to manually turn on disabled accounts
* Show total played hours in the legend ratings (trends page)
* New accounts status - SLEEP. The account is automatically transited into sleep state 
where there is no game activity in the account for 3 days. Sleep accounts will be updated once a day.