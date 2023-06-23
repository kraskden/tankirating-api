## TankiRating 2.2.1

### RU

* Пофикшен баг с трекингом дневной активности (раньше могли пропасть данные за последние 3 часа игрового дня)
* Добавлена кнопочка чтобы вручную обновить аккаунт
* Информация о версии в шапке страницы, плюс ссылка на чейнжлоги
* Сортировка на главной по-умолчанию киллы/время, а не по уп, кемперы сосатб
* Время битвы выставлено в 13мин, параметры показываются относительно него
* (2.2.1) Изменен график обновления. Включены частые обновления FROZEN аккаунтов

### EN

* Fixed bug with daily diff tracking (last 3 hours in the day not counted)
* Add ability to manually update user (just click on update button in the user page)
* Add version information to the navigation bar
* Change default sorting in the home page from k/d to k/t. 
* Adjust parameters to fill 12min battle time
* (2.2.1) Change update config. Enable frozen account frequently updates

#### Misc

* Swagger api is working now
* Remove unused data from database, fix bug with snapshot sanitizer


## TankiRating 2.1.0

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