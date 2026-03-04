# Project_template

Это шаблон для решения проектной работы. Структура этого файла повторяет структуру заданий. Заполняйте его по мере работы над решением.

# Задание 1. Анализ и планирование

<aside>

Чтобы составить документ с описанием текущей архитектуры приложения, можно часть информации взять из описания компании и условия задания. Это нормально.

</aside

### 1. Описание функциональности монолитного приложения

**Управление отоплением:**

- Пользователи могут удалённо включать/выключать отопление в своих домах, если уже произошел выезд специолиста по подключению со своими датчиками.
- Система поддерживает управление отоплением через уже подключенные датчики (100 модулей). Самостоятельно подключить свой датчик к системе пользователь не может.

**Мониторинг температуры:**

- Система получает данные о температуре с датчиков, установленных в домах. Пользователи могут просматривать текущую температуру в своих домах через веб-интерфейс, если уже произошел выезд специолиста по подключению со своими датчиками.
- Система поддерживает мониторинг температуры через уже подключенные датчики (100 модулей). Самостоятельно подключить свой датчик к системе пользователь не может. 

### 2. Анализ архитектуры монолитного приложения

Язык программирования: Go
База данных: PostgreSQL
Архитектура: Монолитная, все компоненты системы (обработка запросов, бизнес-логика, работа с данными) находятся в рамках одного приложения.
Взаимодействие: Синхронное, запросы обрабатываются последовательно.
Масштабируемость: Ограничена, так как монолит сложно масштабировать по частям.
Развертывание: Требует остановки всего приложения.

Всё управление идёт от сервера к датчику. Данные о температуре также получаются через запрос от сервера к датчику.

### 3. Определение доменов и границы контекстов

Домен: Умный дом
- Поддомен: Управление отоплением 
	- Контекст: Включение/выключение отопления
- Поддомен: Мониторинг температуры
	- Контекст: Получение системой данных о температуре с датчиков
 	- Контекст: Просмотр пользователями текущей температуры в своих домах через веб-интерфейс.

Контекст Включение/выключение отопления:
- Сущность история изменнеия состояния

Контекст Получение системой данных о температуре с датчиков:
 - Сущность история изменнеия температуры

Контекст Просмотр пользователями текущей температуры в своих домах через веб-интерфейс.
 - Сущность история пользовательских сессий 

Домен: Система отопления
- Поддомен: Датчики включени/отключения отопления
- Поддомен: Датчики состояния температуры
- Поддомен: Регистрация датчиков

Домен: Пользователи
- Поддомен: Подключенные пользователи
- Поддомен: Не подключенные пользователи
	- Контекст: Заявки на подключение
   
### **4. Проблемы монолитного решения**

- Масштабируемость: Ограничена, так как монолит сложно масштабировать по частям.
- При добавлении самообслуживания и настроки индивидуального набора модулей, будет возможно проанализивровать нагрузку на конкретный модуль, но невозможно индивидуально настроить (добавить/убавить мощность на модуль), тк это монолит и все модули в одной карзине.
- При добновлении или добавлении новых модулей в систему, придется перезапускать систему. Пользователи какое то время не смогут управлять умным домом в принципе.

Если вы считаете, что текущее решение не вызывает проблем, аргументируйте свою позицию.
Данный пример монолитного приложения масштабировать можно довольно долго, тк перечисленные функции:
самоподключение, управлять отоплением, включать и выключать свет, запирать и отпирать автоматические ворота, удалённо наблюдать за домом и даже дополнительные новые - не сильно усложняют текущее состояние.
У приложения 100 веб-клиентов, если нет результатов аналитики по многократному увеличению клиентов, перестройка архитектуры просто не рентабильна. Доработка текущего решения удовлетворит новые требования.

### 5. Визуализация контекста системы — диаграмма С4

```markdown
[Посетите editor.plantuml.com](https://editor.plantuml.com/uml/RLBDJi904ButwJkid30fzEB94mdnfWdHU6uAR6YJ_f3kDk8DIw87Z5oyg_45Y42ijC0hpBwHiriafFRGTFFDV3-piot6xOZ7lgTh9sxGy-8-9GxdGtPkcf5zrnout8cxCQDHBmmu3NYp5_hcqBC3IMemtlVShja-QtGaT7jrQVesupH2o6fBneWtXqfUrpeqOc5GbsBC83LuWorauXc-yVy5Sv702Z98QxBs2JinXZdY6Q8RI655n0Hos8Wfv0IMoCdbLplLjPjxjFJhpCTHB2VqQQ5GDF_icvNi1wJYGHdbC9U5JCo84a8BnBTeck9vWTLzsmvMCeb8n4ICnPH0AeEKqacLhUHZ-o7mSJZhOiJh3hMv6mmifg2AF2K9CLEXNvNx4qgkqHnDrnZx1O5zawbu9R12p20bgUe9iUqTyKKHJLKU2yNb4HjN9gVJjMlg_Qsql8339OaPHakCqaqZcaWDgN1C_NzD5Q-XB5QipI1ugiXUH6tHe8zlyHS0)
```

# Задание 2. Проектирование микросервисной архитектуры

В этом задании вам нужно предоставить только диаграммы в модели C4. Мы не просим вас отдельно описывать получившиеся микросервисы и то, как вы определили взаимодействия между компонентами To-Be системы. Если вы правильно подготовите диаграммы C4, они и так это покажут.

**Диаграмма контейнеров (Containers)**

```markdown
[Диаграмма контейнеров](https://editor.plantuml.com/uml/jPRVJXD15CRlc_GTnbxL1EYDLrwXe891hHIkcsqxQJVP_idkB4YC2HG960maHXDZDCOOussj41RQmYlClf7VcTaktIrLQ2gXsUaovtU-_SwPmyuwmh25MxMIYLjchMYv9SugGZISkvcCRQpFb4rHSGkkm-rYlIPuJSmKwzLCmp9g53JjY99b5Z9pTwQpT6lroLAcQZY2sxYLdqE4OTQuFTDG2PA9BBUTUYrDE6UAfUHNUIux_bjvWcjRjlocF9OTwQLkq-RS1a3L_FswMoiPzaRQgK9flbAlSWhzBhkov-_BKoQF4DrDtMOlamc6dp1jUdKHEn_QIdWfXVKoDug25l8ZWf7Dt_HV8-UflyNanLLY_4CI4yMM3S7NZGrWxcKNsIFzZQYvXctMocpEghkbmVlo0pHwiWVe8GFttD-LPxB5PDlVGR8ZMXvHBeQ7ykIPluajMwGEGZoi8AWl8ox6CiiLWHnBT6KvRg-PHPxI3bq0q0AYWoVf0NpC5BSDyfl-BNBkgWS4WPvWW3WAsGQK13VnXwukNXJdkFOQDoqqI96mkO6l4S5V0FW56mvWHaW_qUQWmhhASJXQf87W5LouRGZNZZ8_8meaPVgcgi5lfYhRHRt9ODJPtqORx0-ZAzmGg2Sm2tfr7NkH2EJEiBqn-dmXNIg0Csy8es0uYfcjEw9iyzpp9PNXX-8hzMG1BQdZoNBL7Jl_Q9TNT8h2S_JWXT3DadVK3ShmZRO7ThI6cWEzJvlUAuzsAPFqcFh5FdLQ0ckFrRa9j7L9cTQDudMriGCQgBb6ImYRwN-a7cp4qH9wyJOCVFYa-fj6nlwKgiUbj5WUwicH_17TD_IuCMRGZ7yhNgJr9bYyP6APM_sXhMRgOFFxX_w1tvnYwe26HmrtLGaNLbQoEO8GWc8lXwTyXvmd-63O8csJOPfIoEFSiwUPC4v7Xb7zSOU3tKAMFNosaP7KUhHM81eQIY0QMbjBkBwAa1MCkKcWOYDk4iXWi4q2TJd8ngHfNk2z6blofwgqAbm6_UJvUxHK_mKnlWOKt2WWwkCD0hLRDmW8FRbcJCI2-1oD70VjfHSx47HDwLU6g3knUJWUAkhRY3at7bex6njfuw52puU7sSs1oSGitkhmOla7)
```

**Диаграмма компонентов (Components)**

Добавьте диаграмму для каждого из выделенных микросервисов.

```markdown
[Light Service – компоненты](CkAZudkIUNAZuTQFXsXu5pGUowZfbmtmyNo3JriQwKRS3oGMQIgPVR_WUnJHqWcocm4NWUtc22-PO3iuXXXjr39TuTY5KRUZ13KIvcMp6n7Isut6Htrf5rsT2fw_cyBd8xOga1pY3FiMeFVKH8wzKYy7TGv2gCmbPioNViNSHOgcmofudRuHeDOJZrPZf-itgk1s-mGL4q4V03aFCM7s2QmWPTDK19hg0S2B1SZbVBMFOiYtjiXrmWYZuWQm9RhrUru0_u8_whE7WAvnIjBMBmjLBA-zatdLgsx8KQ0Cv_uH8jNGfjvGqLYsihOVnFQGKONNaT4hL8gy8cKLCVz5Fww6z4YMEKR8Zw354wY7taFv2iMxL4N4Ogw7qr6zwLLli9cmmmpgTpEBYhLFXkHdsFfjX-1csEKyZ8dpNvIBrDeUMCNKttfYQyBdQqvf6L1MoQJCa4i8Tch52TKI5jeTTc9dE5YH4qbGfhr6UCGjgJ5NsCKjh7lM8uQ_MAMHgNoSCKT2RLCvx2fFmzma8tl3Jx4Ly3XpfY_4nelQpjSBIe3rKL9loCRWUTdrAGTV18mLTviJVr3Oz-2lM9XkzmedMY-IZ4TGBW5tk4b-6ku0rZCOcXgk71WSPVA2s5xcRhgNUD_WraaLrr1VLdcCQ9Pca65Ukezh4uiNcPi4Y78AWsD5omGcEsYN-keF-uhPHo9hrwgKn4JUCcHWX-8uZlQSEyRUFIQc34Jq_D2k6db9ZTZRbb8mpWOzZZePccXp5aMAu7mU4YfNs8Ku93kC6JOUpTBVehaaetVCTTlD_teb_0gal3FYjvTM438o8r67AZIwEGO8KT_gQcEXP8b8qrJKwSyczbn1On4iLR-T_)
```
```markdown
[Gate Service – компоненты](https://editor.plantuml.com/uml/TLHDRzD05Bn7odzOSaghjhvmujJY5cZLGeXRhf6Jh59BZXsjDvGA8JKfYA9MH5H5wW5H8GvSJMXKzolz2s__4U-j7SS99OfbUxrlPdRUx2u6qXQotNJpkGUELtFRTSwsfMm5Zmn3s3iB3KTkjwljW8kQxqdkoOMQtpHQhkrHqNmWwwvJDSo7yoKQsjfODvfs8Bd0eOhfDrk-HpKjJP3FbRW8VAz8SC4SAy0P3E5Q7S45tliGgYuCu1gYmam-PoATxNXS5BTMSUeJeUdh1NmkSxicwG5EuPPgr9vwZvMNgiFWxZv8rPk4RDYIxzYx2B5KMcLFupT2j5h2yHhCTFrsFJiEN-4S8hX5m1v3pA7w25SGCkYh3qXq3k44mNArMAzYsR8jxQeTS88e-O5i26wzNDU0t-6t-gJXu0kSKBMrYyOrAojlfDPN2VGxpVq1aUhebTneJgJVmwkhWKveaIWdGa4Hqt_LYMr0lL5Ym14wTGW3XWW3b1pI9_JeHXqITKPwvR7VzkgssDKYa81SO1ONhvqQByomj_aSmr_QsoBbGlYkoqKosKm7Cevkl3ArsZDK7cboN0fAwJ9K4SATNb177J9QL-hyp3HP4Ej8c4QgnZIP4RILLdYB99rvz688VxH78LFxcYz48w_H73Afbt4LFzp46Ppa5hpb9yHbV0mSwOjiQAZPRAsqb7I5bjCbAWPNQO-dh7CTl2MOovoKctf8ftp5scJZLpZMHf5lb3UTTjJQnv41m-1ZpySX6S9bDYP8_8wQNERkQE_fJGK_nqQedlgikdDCLyUvmN8SrIbxjhbPiWY78AXsl8FW68DrWLS_QIXkAIfPiruyDoRgqfg9oC0t3F2UEi1h7qcfPn4ztnSEWo2CsTcHf3YxX9s578TaMjj8IJeZwIcSZhOCdHmnzpzpqsxzRtOyFuRKsnz-QHyrjO4LaJhGSoapU0ma12FSgKq_qPlOZ6YgEtGln8TOpB-89oGUjNy1)
```
```markdown
[Surveillance Service – компоненты](https://editor.plantuml.com/uml/TLHDJzj04Br7od_OvXGGu4jFFK4DRK7Gfd7eDNAILR3asD7QWQAgKWYjIfKAL4Gbr5P5LG-z1Wh2V8M_CFkFEZD972T0fCZUySoyjs_UxcmGsYfirDnqwf7ZbTr6HOgrCAm7Jmn3sHipLITSQvGQWLHbtmkb5ywK_PfHTssFYgQ3iE8w9SDyF9sZqEhAib6pWr0g31LDlrRtFQgfCq0wbPCgyBqijGkcH0QEe0jN-WkSu_C4Ehe5Pt05KMOYdJ8HpdOygRAhYvZwJ35y9OFlUMcNGtg1GxYX6jtK7x3oGcy9kBslfTuRRLcrGxbXRsABkToYUDvRKKUhhXolAapNRrIISVW6fn313JRS4zYpgp_19NG4dEYF27GAdH60-LAsKiAoUJkqItOWgKNE3yAgajRhPMxu2uxrPsu7N-60ggrD5AvMN7WRPeE6MfUEYvAMPJ7WEDNyXaXlyOwk2HlPm35kRXVnpt0LuJOfxJl6hrcLzfGWRhg5T2CapCecU1QV-WslOglD4KJI8R4KbbJhJbbc9iIxT4hWBnvibaoXVDULQgp8Z3yaP5rvOpB18zraCIFQ1n9j2HnN1suntCKDjWK-Mh7y4-EWGO_F6EA0vH0k4O4VEAL_h04ArqNjcomUuf0iE4VyHYfs1Qxq5gUqyMC45tS9A5dtnz3p60gSq5T96z7unL9kZXaSyDvQUXjk1Lp6Kn_JqtNmWUccNPFADdd9db-m2aQ-OEN4C0RxB1kfs6No5-mEsigPeBDmEJ02FoFQseWTE4eatfChyj8Td4m-Sl279zB1XdY0zAvkePi8Yfn6Hw29qHOHUL4ev2pgGoseTdY-O5-tuHp_9_r9uu6ZaYNhrKjZf2wk6V4K_4I-JRs3_sqUnpc2u_jzhZ6I9ZOc9mVKUgOdZ6JheOl6EGuOnHdzUSCX6YE-NteSxkJ64tmekvVVQycN1FnbFHdQm8f8xt0k-GWlYpx0eEy3LqFl9jt6mVOFr9YX2O3CHqAndz1Anb2SMRnQyOx-3m00)
```
```markdown
[Temperature Service – компоненты](https://editor.plantuml.com/uml/TLHDJzj04Br7od_OvXGGu4jFFK5DFq3G1X9wZPna5ImvjhLUbwAgKaXQGKLLr8fA7AgYgWUk0H5Xe2H_OVOVTMQTs76WaQ9TZtRUU_jcPXS3QGaPjfnyxf7jrfsmmTcsb7xmn324jRFGjELsM0i3BkgUAxah5-fUo_0TowMa-K0s7Bjcc8_dInJQMbypMbOWkS1GrVHQlkTIZgy9yhaI5u7d5Wakc6C5E8K1tAelS8Nh1VHK1_fm2r5X9fypaSwoNIwAMojuzBdGz8q2xZUvLPUqWHEuenpLLXynyrhjCHW-1AcEif1DI_8TQnSXbaehx4NyHOXbNzXkavcE5pOcu_03BY62EmGyOeWvKFjm0pq65-eJ4br2BqEmN2isQfYsR4chPWMS84fU89k2bpVMDE0lE5UVDHnyWsFABk-YSQtgitUoYAjV3VGtdVqDaThJD_cBdAFDaEvD5rHTlF-X1ZtMt_kez2PsSK9JzQaNkWrBx6fqF5t1XGjBXeApCXTlxJelpB3t-Hp3NrAx8jLTU8x3HJR7JE8Jngs_CRMGKvH6TaMa68Ls61Qa1qCC3_0gXmoNJcBmp3Hd4Cl94eurfcmJ4VJe737R32A6H5WeQYIu-fzRzoW5zxqitoP60bjwOh8pgA9ijRGqifikWv0mP72J57BAFyV6XH2u6tZ2r9-KlhvHgHWlAvKIIsDmflMISVsubx7Bj7KKkcRKQKc5iS69xWELUPCxux7Iym9_jDazd07iVlLLTUOOsZ0W7Ds_ROnsY98Kb0c782Wt7GxuhWxX2lyNeoBYj53AQldrAoEJb-HakWL-ejgsEi1_5qcn74YE-uSQmf2fyyRix5XPtCH4COcSziUqnB6WvCIehd2YzjAt8PPmxsnIAfnExPuwGD7ZosUyZr9XCOEUTpZJxkhp1j952D2FRQQv7_6DQQQcF7uCkqYGyc6vxl1o_Ko_nDIB-1hYi_eF)
```
```markdown
[Heating Service – компоненты](https://editor.plantuml.com/uml/TLHTJnD157rlqlymzgaGO5zyyabSK22WbGLVcsqxAPjiTvlPgKYC2HID68YD1XCIZSJuuEkA45OAvI_S-KVUEzlzQ8jDDpjpzzvpplsOUHX8MyXEooqMxZbUtUqqEDkIiXqyC0nXRyyr7RdLgNK2Bkg-9xadv-f-osYxjaT1iu5ik4xDCE_FLiYqkRPgjEn0Se6cgkctshv7CMrDK2nKk0XyhqnmmGmhmIaCeA-Eu1BVPn2gBbn07wBILB5W8ftjU5oKDvVHzR7Gz8qIhjUvNPUqW1EueHYrgzvYv5-rn-3sBaZL6uLisf9lspi8CLzPPa_Y7I5QRU5uJMQwVgUHjyCNE8S8RX2mnn1peDx35OGCpjGx93g7S8HWeLPkr31imPPspGuuGLJyG3O5jvwlQi3ly5jzq73m2Oufsjh1mhMgYwzaUOlR4dLK0sqYznyGgJsTp3NH3aW3vjf7mmr-wcdCOyeNbKMeBMBwh_RYYcYlk1P7MBH3k628T87gG_g4vRfMXwGYbqNraT_n6hROIVGWnrAyOXONBvqwBqsnryK2mr_Qx38DYl1TbulCtqnjkIgllJ2rx5ErgsiRKMwegSjGJWYtQ1vWqeSwtRGRK-DyGImb8qkqPKmv2tp5SlpH9GgPsjSi8KvAMic9ubBD9ra5R_iPvJhk0aVw8Zz5r7ssKfaVzePowH8ZWwkqwMEbSnryPR0Bd6IRsgedVz7QODaMVexBfxPUwGJwcCArWPi-bL9tESJwwhFrXjgypjta8EeJbWTJFVLHTMTeH0R3oHdW8UkfBj4kRMnKBC8X28hDZXDynj6wnETis4ayNnIoOZrxQep4fJ4ZumBVS8HtrG4--qXASndH-gwfC8PJQKnF9whY0IRuF6es8EFo4Z6fnx2hS9AS6xf9Ole9txHP__EE_MD8VHt0BrrAJMrWHAGEj2-D35uBGu84T_8I6EbGN99eh4VqBiNtMYpX8LwQUFl-0m00)
```
```markdown
[Gateway – компоненты](https://editor.plantuml.com/uml/hLRBRjD05Dr7oZyCsT1MRRrXXLIf4D47Qg5gKhQLcrY99SUEnkDM226rAI1GKIDOiG0gn0-4qD3Gvo_C_17dJcpNThm8KYjP7a_cddlkuzpfeYzCBeAMKopSizsQ4zGjrXIYxJyq36xkpJTiqGns0j_YDSyLbYlcQrxBQ3kcIqPplgWxzevHVZ2tGLjRwsj6o_I5nR6rNVPQRSybcxPsK2niMDptt2c2ysTPIPx8QtcXFieplFkofpfo82_ai3HTB9JXphHTYqzjhUBe4jVkwoMiDosp9cWXlyWhib7xwWqi_we39c_o85MN89EWZjre2a2iqPjLBBvhrop2V6hkcXhxfxnH-x87a0j0NCcX731vhGwmCL0VeYrvcIRRC8K5wsMy9aVj0tSVJmS_N6R3zmE-QzaEqbiZv4hYCzV3Du3yGiZ7ylJMmvdiCNnV0_uwIrbOhJOCgtXPt1G1pyVz2akWWIIH7MBzc-diNYBd0mfATTGXId6KXczQfh3T1Y1MHgl_oJQS0VqYdUq4-lPZBt3h9d-f4xzdlWJMeurLjZpwAaspLyK2mr_SYrFKnznp78k7TSAvShn7P2fjJZJNNvGrjPCmt24YxI7I3Wg6Dg2wgRVWTwgww8SepJeEj8awqlnPtBZJMIBS2uGciOa572RI4Z8eErvGZsCP5Q673B_7SuZS3CcXUeUYT5E-gVMPF6TGnw7y0-gekJfar5loF3n25Ul3ye1-kABseDqhhHf4SZDo4B7ESjVoAJisrf68GlgZJItJ9vKgk_sKtm4O5rbrHvdCPApFqe8SIngLEVPBcRaZjod1ZdbDQZ3sdbJY7R785VSO6n9ip28XspiYaJS8nZY4gevfXDg-8mhPIG7thqdtzzd2mWAJFw2yBgo6z5aiR5fETANeko8vJA3DOzMPPNhmnUCBknsYirAjRbG8dI38DZKSv6Tr9C_mz4FTOxBeeLLv_io8JKV6ISDOsBZVI6O3JDwU-fI9D78RdKvBIe_nRA8CVSQOcSdQfbIHRqf7SYnpEpiV8Naq1odLbVa8uhQpSW2oBPMF4PuAxKS8gTH5D_kIn_TCNXyRZ57T8_V9t8MtzwIcUSdBNDUJGYMp5rx8avfcydPxtqu2K2miudB2lsV_0000)
```

**Диаграмма кода (Code)**

```markdown
[Диаграмма кода компонента SurveillanceController (связь с SurveillanceService) ](https://editor.plantuml.com/uml/XLAxQiD04Em5-eTpvn3BQbAv20QH8k10Y4aTJdTh-U1UxEufXV6_voIJXt2Kj3CxipFiReabSdAsB1R6AviqY2Dpf4rTetnVTuQFgKq4g89dyBnMmTNHIZ-8Ac9jJLitTzNp0Bq-xMedYG4pzDO45uCVD75SK1RAIYAnJzY3iLcWeCck6Am55AUo4EAs0swa0vIqlFa4Na05rCPtj5GZzwWlt1Z-YxqYGvpZikb3dvDz6wx40ODR2GvvuZo4BGkJ6-D1AfX4tWFs9cENl2VP4gDKV1PNqQVSR8iVOtyMcipDTvjk_RVcp3MgwlxNrXkHdu46uHQypdVz0000)
```

# Задание 3. Разработка ER-диаграммы

Добавьте сюда ER-диаграмму. Она должна отражать ключевые сущности системы, их атрибуты и тип связей между ними.

# Задание 4. Создание и документирование API

### 1. Тип API

Укажите, какой тип API вы будете использовать для взаимодействия микросервисов. Объясните своё решение.

### 2. Документация API

Здесь приложите ссылки на документацию API для микросервисов, которые вы спроектировали в первой части проектной работы. Для документирования используйте Swagger/OpenAPI или AsyncAPI.

# Задание 5. Работа с docker и docker-compose

Перейдите в apps.

Там находится приложение-монолит для работы с датчиками температуры. В README.md описано как запустить решение.

Вам нужно:

1) сделать простое приложение temperature-api на любом удобном для вас языке программирования, которое при запросе /temperature?location= будет отдавать рандомное значение температуры.

Locations - название комнаты, sensorId - идентификатор названия комнаты

```
	// If no location is provided, use a default based on sensor ID
	if location == "" {
		switch sensorID {
		case "1":
			location = "Living Room"
		case "2":
			location = "Bedroom"
		case "3":
			location = "Kitchen"
		default:
			location = "Unknown"
		}
	}

	// If no sensor ID is provided, generate one based on location
	if sensorID == "" {
		switch location {
		case "Living Room":
			sensorID = "1"
		case "Bedroom":
			sensorID = "2"
		case "Kitchen":
			sensorID = "3"
		default:
			sensorID = "0"
		}
	}
```

2) Приложение следует упаковать в Docker и добавить в docker-compose. Порт по умолчанию должен быть 8081

3) Кроме того для smart_home приложения требуется база данных - добавьте в docker-compose файл настройки для запуска postgres с указанием скрипта инициализации ./smart_home/init.sql

Для проверки можно использовать Postman коллекцию smarthome-api.postman_collection.json и вызвать:

- Create Sensor
- Get All Sensors

Должно при каждом вызове отображаться разное значение температуры

Ревьюер будет проверять точно так же.


# **Задание 6. Разработка MVP**

Необходимо создать новые микросервисы и обеспечить их интеграции с существующим монолитом для плавного перехода к микросервисной архитектуре. 

### **Что нужно сделать**

1. Создайте новые микросервисы для управления телеметрией и устройствами (с простейшей логикой), которые будут интегрированы с существующим монолитным приложением. Каждый микросервис на своем ООП языке.
2. Обеспечьте взаимодействие между микросервисами и монолитом (при желании с помощью брокера сообщений), чтобы постепенно перенести функциональность из монолита в микросервисы. 

В результате у вас должны быть созданы Dockerfiles и docker-compose для запуска микросервисов.


# Задание 3. Разработка ER-диаграммы

Добавьте сюда ER-диаграмму. Она должна отражать ключевые сущности системы, их атрибуты и тип связей между ними.

# Задание 4. Создание и документирование API

### 1. Тип API

Укажите, какой тип API вы будете использовать для взаимодействия микросервисов. Объясните своё решение.

### 2. Документация API

Здесь приложите ссылки на документацию API для микросервисов, которые вы спроектировали в первой части проектной работы. Для документирования используйте Swagger/OpenAPI или AsyncAPI.

# Задание 5. Работа с docker и docker-compose

Перейдите в apps.

Там находится приложение-монолит для работы с датчиками температуры. В README.md описано как запустить решение.

Вам нужно:

1) сделать простое приложение temperature-api на любом удобном для вас языке программирования, которое при запросе /temperature?location= будет отдавать рандомное значение температуры.

Locations - название комнаты, sensorId - идентификатор названия комнаты

```
	// If no location is provided, use a default based on sensor ID
	if location == "" {
		switch sensorID {
		case "1":
			location = "Living Room"
		case "2":
			location = "Bedroom"
		case "3":
			location = "Kitchen"
		default:
			location = "Unknown"
		}
	}

	// If no sensor ID is provided, generate one based on location
	if sensorID == "" {
		switch location {
		case "Living Room":
			sensorID = "1"
		case "Bedroom":
			sensorID = "2"
		case "Kitchen":
			sensorID = "3"
		default:
			sensorID = "0"
		}
	}
```

2) Приложение следует упаковать в Docker и добавить в docker-compose. Порт по умолчанию должен быть 8081

3) Кроме того для smart_home приложения требуется база данных - добавьте в docker-compose файл настройки для запуска postgres с указанием скрипта инициализации ./smart_home/init.sql

Для проверки можно использовать Postman коллекцию smarthome-api.postman_collection.json и вызвать:

- Create Sensor
- Get All Sensors

Должно при каждом вызове отображаться разное значение температуры

Ревьюер будет проверять точно так же.


# **Задание 6. Разработка MVP**

Необходимо создать новые микросервисы и обеспечить их интеграции с существующим монолитом для плавного перехода к микросервисной архитектуре. 

### **Что нужно сделать**

1. Создайте новые микросервисы для управления телеметрией и устройствами (с простейшей логикой), которые будут интегрированы с существующим монолитным приложением. Каждый микросервис на своем ООП языке.
2. Обеспечьте взаимодействие между микросервисами и монолитом (при желании с помощью брокера сообщений), чтобы постепенно перенести функциональность из монолита в микросервисы. 

В результате у вас должны быть созданы Dockerfiles и docker-compose для запуска микросервисов. 
