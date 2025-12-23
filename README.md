# Project_template

Это шаблон для решения проектной работы. Структура этого файла повторяет структуру заданий. Заполняйте его по мере работы над решением.

# Задание 1. Анализ и планирование

<aside>

Чтобы составить документ с описанием текущей архитектуры приложения, можно часть информации взять из описания компании и условия задания. Это нормально.

</aside

### 1. Описание функциональности монолитного приложения

**Управление отоплением:**

- Пользователи могут устанавливать целевую температуру в доме.
- Пользователи могут включать/выключать отопление вручную
- Система поддерживает управление отоплением через прямые запросы к устройствам
- Система поддерживает хранение настроек отопления в PostgreSQL

**Мониторинг температуры:**

- Пользователи могут просматривать текущие показания температуры в доме в реальном времени.
- Система поддерживает периодический опрос датчиков температуры по синхронному запросу от сервера
- Система поддерживает хранение настроек отопления в PostgreSQL

### 2. Анализ архитектуры монолитного приложения

Перечислите здесь основные особенности текущего приложения: какой язык программирования используется, какая база данных, как организовано взаимодействие между компонентами и так далее.
Особенности текущего решения:
 - Язык программирования Golang
 - База данных postgresql. Используется драйвер github.com/jackc/pgx/v5/pgxpool
 - В качестве HTTP-фреймворка используется Gin
 - Приложение использует слоистую архитектуру с раделением отвественности:
	1. models - содержит структуры данных описывающие доменную модель
	2. db - слой, который икапсулирует в себе всю работу с бд
	3. service - слой бизнес-логики
	4. handlers - слой, который овтечает за работу контроллеров
- Компоненты между собой взаимодействуют следующим образом:
	1. Models - используют встроенные или дополнительные библиотеки, могут переиспользовать другие модели.
	2. Services - испол


### 3. Определение доменов и границы контекстов

Опишите здесь домены, которые вы выделили.

- Домен: управление устройствами
	- Поддомен: управление отоплением
	- Поддомен: упарвление светом
	- Поддомен: управление автоматическими дверями
	- Поддомен: управление удалённым камерами
- Домен: 


управление аккаунтом
	
- Домен: управление списком устройств - всё, что связано с добавлением нового устройства в систему, или удаление старого устройства из системы
	- Поддомен: регистарция/удаление устроств партнёров
	- Поддомен: регистарция/удаление стороних устройств
- Домен: Настройка устройств - всё, что связано с управлением устройством, сценармиями.
	- Поддомен: прямое упарвление параметрами
	- Поддомен: проектирвоание логических сценариев поведения
- Домен: управление телеметрией - всё что связано со сбором данных с устройств
	- Поддомен: сбор данных (потоковое, пакетное)(в реальном времени, по расписанию)
	- Поддомен: Выдача данных (текущее, за период)



### **4. Проблемы монолитного решения**

- Жёсткая связность - для добавления нового функционала потребуется правка основного код, что увеличивает риски ошибок и замедляет разработку
- Отсутствия возмоности машстабирования 


### 5. Визуализация контекста системы — диаграмма С4

Добавьте сюда диаграмму контекста в модели C4.

Чтобы добавить ссылку в файл Readme.md, нужно использовать синтаксис Markdown. Это делают так:

```markdown
[Текст ссылки](URL)
```

Замените `Текст ссылки` текстом, который хотите использовать для ссылки. Вместо `URL` вставьте адрес, на который должна вести ссылка. Например:

```markdown
[Посетите Яндекс](https://ya.ru/)
```

```markdown
[Контекстная диаграмма монолитного приложения](https://editor.plantuml.com/uml/dLHDQnfH5DtxArvTMV23GbTP1QIBGWkX7skPw2CEZAFCUz9qft5HYaLRABIhjg5_O4Wr7QCP_y9z_wZdtfaQQKoG8ZBltSzpphqpXyPwaUrtWi83Fsm6_PPMRMjxvg1MYxmtrHFVjll7VQEZPZUqEhJLPhTJwmLUo3aLOrk1VrohFwuSiUdrysUrZcUiZc1gr3dZr5PxNBrWVHjeHUUKkA4xepcjwDgDQQ7eYb8SsNJ5hWD5Fza9wrnH2aTAQrh2VKq9JANweuU5mf6EJ3SiCRAoAj9t12tT1_gDvmN5KWttelZS06M6g0axpWb7qDoTgRpfoBt7Vw9oM6lu1X8xSWCtKfGer8rf1jidPD4b5-SIjEAy8k2yV0lMdPBfGCn6MtlM3q-uztrCDk0Is5AfcZ54fwMRAgIcRE4y1iH0t8ZMV0Q5l4uMc806M4BB9Sx25BVL3RJ6arDRmhEd8y_s8zqmEZJTo326pqBs7HVX5hjK60lOZtGXTPbHDhwvUjfzLK78KAHDQI7JXS1b1SbZvY2Mr4t_3eNJ5OBdz2jR1fQ0bNOJd8TCACdasb15yeILNml5MCOXt45mYrykVCE8YH5_oqLFjfh7w01iDtiO2xcli77z9OXDPJraCrVIQkR6qZs3CCEFrRdCOKms2u8hYxEuIqNCuuKEyhMzjIrVxjcoExQph4IbVVORpRdtpYMu1UN7VundhzSfH_5FmzsJssgG3MG_JJOlqAvPySeSwh25Zz8V)
```

# Задание 2. Проектирование микросервисной архитектуры

В этом задании вам нужно предоставить только диаграммы в модели C4. Мы не просим вас отдельно описывать получившиеся микросервисы и то, как вы определили взаимодействия между компонентами To-Be системы. Если вы правильно подготовите диаграммы C4, они и так это покажут.

**Диаграмма контейнеров (Containers)**

Добавьте диаграмму.
[Диаграмма контейнеров](https://editor.plantuml.com/uml/hLTTRnf757tdLqnvSYHZrAfFUKfDgjPHKf7OVKOBJF1Ai8js1pjMLGdyaRIYifieKgigQjD6LL-n0pR16F-5sN_KS--iOTbTK3_io3BSsJdtd3jtpjtSyvNbgLQZdld8TYhrLbMARQMQ_jrythDsrsgsscwLMxxqAgwZfAFMAcuZtwnR3kt9-QfQjylvmgUv8eM-VlGmtx1y9Js4IWNii6n7UcjDmiyeMzMbqB_eYRuAzlL0Vz1NUX2qWwu8EdeOT3Wu3hftXNwdnrYSw87G4uGd-bfVOl5A3n5QANno9vCfIiztdHNYjYgo-ZSyT1dye2_mzqpt60lVixmMj07J0IRY-3oa9v1wN_1Ze-0W-1w_n-Rh6Cb0Yvyz0Bq3eOS2k3tTH-mbTkbp0YS8qCM-BEXixa5texJkjfogvUsjL3wcpCjqPE-8Rp82FzD2hUpASibgDcddApnscjFNJFSIEy-d-x3yH5eLbLsY6sf7UFW3k9jgGyWXrJpe2EYu34v4SC3eFMmBEI6OfAd7a3TFi-6MxRgyOVe6pvyIIR08jIvZBRwobBqZiucjGVTMwSOuMqsxLBEKtBNsaFcpueRumdmZ8W-i7Ik_sVHifoOATRTLZIxgFv3b1ShP4_eiE4AwFdqqh13fWEsHQP3hiD5m54AF12-rGHZDZTK5pAfonwx8KgFMK9JjzzcUTCtek0wtyOJA0_tKp6CzZ0jPTrtJ8c-nzRsvODpMpmbcbS0X97AlqER9X8WlJ1ZlYr09hEAfNOjh0LXlXaFTcGAyKCEx55uuzXu7ntINmqz3bj61o05dR9DOZgTaYmjGiYuRKdbx9N-daZWClbfi48FGHhZcnJsrxJfPy_Wfph_DXa5y1wZmX7F5c5FXhwZbqJbypWkefHUuMgPaBu5nOJen0fPQqgBhgvedDny_P9f_Sgsk8WUFtOV4d2toEIaJT3LJZpuuMgNIfzdE2Q-DsE4FWVAU3FIcuuAZ9VLU9cb1tJUSf_NMi_lhwPBEkFnZ3eGuRJH2tzpD23v8VJkrxy-VAP1gDALdgPOdIxvqVDVpASDh7WWlWCwjcpOfkapbH-GcKo8l7BEj3SI6kvLBg-MgCCuGTia4LGeDdBOBrZ2xkaEQHi4nFdV8UeR6_gON0vkFQJhHsOnC9OrbG694txQ40JkraiyXOlnhQAB3IF8UEKraBEiUY_iPCJvx23lX4Sh7FUPKVOnoMZKK-lX7rJaFQHnFXoYLdggmg8eOfqzaFHpjaPauJKwD1wlYz6TcK2Rc1b1c8FCJAnLd_C_6c46TafeVArp9o7amyfTRMyLST3oOFg7mPWWNfVV_4Hbo3YOsOjwGBmRVSJVjGzM5wInJXwL4qr1dTdzBk0aN_c-u33odVCu-_-9svzV0l07XPkzEtmrd9c7UNXxSNo-4T6FA_oLkOX36q6Fw5w6ds3RTiSMslJnDt8j_IbBFa_rDqoTz6M_2OVrJox_0MDycFSua2zzFmgPFqqJf7ZtUsfe_5fCjQNEtboXHkbHXvjNy04LRNh2aWfl6IhVgk1t473ZjfJ6I9dDFEbNyX-nl)

**Диаграмма компонентов (Components)**

Добавьте диаграмму для каждого из выделенных микросервисов.
[Диаграмма компонентов сервиса "Управление списком устройств"](https://editor.plantuml.com/uml/hLPTInjH47tFhxZDaq8rjFhK9rjR-e52DEbpM9DBN4XsbzqRRIY2qLfRB4YBJwMqMEbxj0QZCV4ltFr7FJD9rYJUjH4CP7VlPMREpDmpPtSkK9QlAkLIue7jv4kLWXIhIdd1arJAjzQdYxPQhQnK0kddNKT9HqtdtNBAAraE-Km5gb2oLrBpirDfsdgxk90gMu6IFhPoysxPSntoyIX-Gjcg98N-hZlwKb_XsjODtGwtmZsXJtaJIrr_8lIHlWetTLsVw1RlDNL3X3NuD76z85CHReSrk6vYSKvF-aHCpCzC9X9fwGUkCq4fFnH9_Gi6hV2BFiFzHDTXsA1r4fQPAZ8jvvwv5QTW-TM9_2DoE48URHWtXEx0kWFKA8daf7YV4FZTS7qCrwVfr-AbfUIwLOqCwHVrOSBox5onQp5YZ-KRQysYk_uDu5sKMHVw9Dn11gVqU8gqzscdfXlXZa11TUvI1nie3GKHrWPVpJdEKF13U6-Y4-WaUZZQPo3SR2mCBdKpfgI2NBFpCaViyDrIYPl-dFTWrD_hboSodcyxHQxofpvcx6Cwb72FOHhXbdYLpQQd1glhqgF5gTqfNqEs7hZkI3_NoneSeVlDljpW5bXG3pVns6OxQlZWaGqGDjpdCh1iwByKZYFLmuz487sUGX4upsjEcRbim3FLerPjlv2Zggh_gudYTZXKcpktOu2zpvh6mZFLrAl3bvuRsChrgzUCMexsXZ8z139Yieemn3QrJ-WBmMa17_own3phOoDTmfqnfcUMaFvmHwZ65ahwJ28H8L2KsqQai48HliByh3lFaL_CIQJTG1LzcLbQI0vYzmKHbtRuWNjysJkBvbWrRaJIz-8TA2JBdlGjLV69EqxW-W6X7V1OxYBe1SAoR99eD_ZSqM6GSmyH1mAjIajXk7C1R_68z_IpsLTTadSQsLgNGnopJbAyB4iznHwMI0unIiDHSK1GWpJGAoDU6WXm2CgaO_e73-4d_BVXtGHmavuDMT09aN9bUh4DuUArvu2wqnijS_Xh1HnFAM9Ik5qkxf36cCDDQSILRHfp_Gs3UZRmCg1l0YRHAS1PVkYxuOmPHVXu0VHWRYljdg4CuOP6d2AJRmlpsMKiHdZabTOdVLntOgRIE0xyGSJI4AK-AjBD4Ob6BGYtk9JDZj4-ys3VUng9EUaKyA7u3m00)

[Диаграмма компонентов сервиса "Настройка устройств"](https://editor.plantuml.com/uml/jLRBRjD05DtdAuQkWaI9UAnO0GNnK942BUl8JORKacD7jbEe45BRK0e2gGAnG0XQ0MBlfWrraoRzXV4VSUwruxp6424HoOddPkRSSr_7lkxvXkitQbRkd6cNhKP5YbNVhtlN2WNNU7gnQlghZPM69zsoO_lIzY-MdLgXRXasdPdt_8fbhXGMhiuNoVJumMAXPdY-T64gBJYrkcFJcJhXvtpJjwHGdrLVdQepVFTKM_MYhUYjK4Tin585ruJwge9e4tzim7gYEYeGKNDee3lL4lc5A-TpkQ9qFSVE4yCBOaxjOqCtUgUEyTi2oXOmiPx3pgLr4AkLRZeDksAuw_do9Jhm0svxsDmMge_TVH3hCezGjUVEY-SvWS_KqSiuUgDuJzmnVFdKM4ytqYSDEs_KpL8rtZ6n7ylxnff1l-ex7EzmaAeLROF14TqUWVOkMJPLEzeM22XGPnG_30WD0P6l5_ojvtY5mB_Xz0Oo4KQRWf8xbjPGazQCK53z9sQrH4tWEfR5oLvW6pODR8Emn5BTDUqgHxUd3W0Vg0CgHlIMYW9AM-Bkyd9nVZIgk0iuwDbuQbXMv9fPbgMwuHerhuIMeELK6iu0JvHEYGXZjdGEHclqWra6G011vay6t9uYf5CGd08Ds3mZRwyiRSCrdIpAEFeg7WwOq9X_PzU5nqEStS5oar8Oqnd38VenTPgLDhBFVH_jQZcw3KkMf5qrRQQtdtZiytL2jGDNzGkB3f6wvJgEvVsn1SXt2slotuKGZ5DNdQYf2rp7BUbELzOTp_GTTttOdOzIsrZAlh4Rfjtd4ZMfWa9ro1B0OOyA2qsOxiOWsfvX-gwIbvySDsb95tbzGoAJeXEATjII9gwi4C9xR3-EzI0zbz6iHSVpgwvSUhWuD-exqJ9a6wTV8jeDL3r0MaFAVryTQE0eHg7fbv78NwI2UlkPdr-LXe-9Btbiug8dOXeE99XHTW597Sr6q21TVYIjHBx7zP8XfXfxGZ5wb9k92aLDm1Bg69vEmTGNYYnwZQi9Po7GGxhNkAASacOj9TWQk4pr-KWPm7MOYPvANwegL02DK6Lxrsd8x8wrWqQUCeBIJPpwW9av7Xrm9avFD3W2Rfj7_PXxeXztLE9bcln_Tp2XPtkSarW4qsOB1zbgieZ6JYTI5IOUnl0oM-4JAomFtx0Yr7VnoqywhBfdLDpiz0BGYnKxksgQx8t9oeSfsOLiJ3oE1xEP4Oj-vAVcjSClTYH4NAFcS4VwjC7P1m-NbtLv_2SdC-Lfw3HtNTeLlA3-1W00)

[Диаграмма компонентов сервиса "Управление телеметрией"](https://editor.plantuml.com/uml/dLRRRXD947ttL_JwAKWRB2vFF07XkWB94FDiJUmciHZFM3Djm4989jaiH45482GaX8W0yJvnOcLm4liNklzeJzNuFZq9mfBjwNPNrQbJfwfzDLHEe9frD_TNpQkupQeKQqermYk5Gk0yExzQKslDbMOeWuhlAUcfynM_NcYuZaSsYw6gkhMLmjBbnI9jFRf_hr1tGYK3R9MN_7h3zyYcGVvpggPSAVHdtTV7Ue3F4ztL9sRTR0jzm9jOwkYAq3_qmBHrf3lwYFTYtHLc7O-qn37Oj6bJ_n8BIvVEvN956OI-jq0u_nPvlGjlH-QjFiHtHqU9gNcRnydb5k3LozVzfbTrWjP2vG8P_83N4npk2jt7wJxWZIFdpuaNEO5NolGYJAyLxuhRZfBFdDRu8Bt6oIyuZLfvDJanTnxBVvod3dthxmZyMlTq97J7R0B10JqU0FOExRn2qfi22KLCJHyRI0q9KQoN_6d7U8cSVuDr6qp4vfMDnpW3kPAkh4iLjCfK_C1tNQQtDDh6qT4svL1igJNV4xTS9tp2ENtLUrp6FIg1sQPIK4nnfrGgBaxdaYY0KnrmwJR01TEFBJjW2znG1TAf0oi0LPGV99dl8K3RRC6gnzwvl3rYpsmJfU0PWKveCKaYdzZsOHrp2WHxSfHqqX7ueOiT4XffvvZgr4vm4dGhnXL7LTREW1W17k8bcFuS878bY16aCg3Ays-nUMV5LNMKKqRtFArL95pVm58i9ykve5b5rR70qpupi4-Wp0RlfykN0M6Yja0s_1cTFSH6M0DVhHG3H-3W5yyFo6WTCGVKDxrfBevFaz1flNEPGlpaR6aI707w5Z4xNP6pEgUwGaxUm-8mQUYngHqLJj_r7hlDvpUkvwV3ZyRWZCQ6VjfcXmTenrvQILKy5SW6SzScAka9ZeXcFrIhWLn-SCyE98RrlwHSW8dCVuHUa20jxl3xvfZlb-CXVFCvCLLloC1HpK12Vrxe1o55-yZgUaqj0RVC5DqPNHOqoeoSjscSFvJkSEhFZbdzQJXT8lC6JaOYPKSzc6_X_SwgJP5KUgwPcBCaVap36R4fz4pGZ76flm14sxoXZa306BuctJ4x1Obe6exBG_TsZtEz2gwI9FOp_GgwLrEpDILbZZdhFTMK-WC8EPpkU-QEE44SrYQ5eHpNMHmOuQbi4b_pJKAU6IBmx5YpG5lliO2jVmVYZ3Wp3R0xvc0qjkPbJ18uLSOS97tPP2Xvb-zodi0Jrl2yCobASYMb9ltz1wNICANKjP4HpAxwtxmNviHdot1U3BY1p6Oo68ObixVKxroM6NcU7NBcoingQwhRLUbLyK_qVm00)

**Диаграмма кода (Code)**

Добавьте одну диаграмму или несколько.
[Диаграмма кода "Управление списком устройств.Репозиторий устройств"](https://editor.plantuml.com/uml/XLJDJjj04BxxARQS4bN15quHGfXWWINo8zjfnIaotcrOoLuRUqqRLPKW75k9Uvy2Q16fQE4Lrc_KsRMJs0cqikJT-VjcPkTdD-LUmhCmqDvGvWSP9kYKypXjwthYVTmSKtwQdMGfIVo8SSBufX-5UXnuJDgqKeu3Ug9tjbe3oHfsZ_JGIpb9W3NgH64SCMaJItoDKnuG9BwBHt6JNyBt5UKNubuyYoVn86RvL3m1FSklKNu5qYaGJ-ANl8axrEXiDJND3xmqHVladFh49d6KKXub4_HPG-WjIhrpqYXa2AkZsIvrbVm3PNXlOk66mnFAneXYaFTZJYFc1Tk5uav5q_m4YO1eQK4Md7zP6a6mDnd2aoqDCqM1qH5DUSq0au1miXRMUKInzgMUi4gp1V8sAbGLkOofnYuyrbZC2qcDmISncR-G2tV5X7xWMJfdEufIWXZwXAs1-WdnEC46XzmYtmlsWN9fM61bCNvDg98V4mvvDok0YbTv-3LP-QvhV1b_excIZP8KAInbYvHq7OaVuXuQRGh_2sX7CKFvPPMpMWA9gygmlKrO5fB4apsm8qlfcjs1QHlkq3P7Zjbp-ZOm3qt3jNe78-VO0RdKEZJjRjzn3HS8fslOxkZ0SCttnl6ggoBCLvmP7TTwPyB5wYsk0xEtBttPve7bk129rUy1UzYpJQDpQEmTcKiNWwAMfIUILDeBhmnNUstSb5fundade33TRTHTtEU5JSYOoZLGyzEeKRPIANAooLb6KWvcwgoFRSPUHYl7fOZNMWoDsWI4uIe-RABGOzYDIjFL9J6VjhN5qchjp8UlZJPW8Ts8MzXIKpYXIw1PDagRYgRg4T0MC_4RjflIcmBn_89kMUGLRRdnRg09hoJ-YwLQ8bI2LIZGltAVFkRVn4D-9V7-2w5IkrHB-6UHc0x4hVW3_pj0kYvGj5ruLLZaVm40)

# Задание 3. Разработка ER-диаграммы

Добавьте сюда ER-диаграмму. Она должна отражать ключевые сущности системы, их атрибуты и тип связей между ними.

[ER-Диаграмма](https://editor.plantuml.com/uml/fLJ1QXin4BthAzHS2g6cTsDC3gPGUWcqFY_oxZWHQAL5EsimII293wLGo3Vq3qngjmkDdL-G_gYZjTVPTUg6qelHphnvy-P9uvCSXSKYLUmLdaCAF5D2QiPUS_VDp_mNjt9hV-DlNSb0eyGf7-PW-GNZ_9ZBX7VvSFX-m7kzqm_zFWKx7Vg1L4X5gHojr6SKq8AO6z-n1O6GH08fYZ85Kf5cx8hLJGOmaJ6yq6QV5QTPwvj8iSXR4R1Ig4WNwOY6Q2HI4KSYIIpaBRmIEKQnqHfYb4R_LUofiAG7NpJdc2SLFce2tcq0-nDDX2fQWK9BV2Qmo9BWvW5rdq3HlQAT3WIA_z2MrVD5XrIEbH4Oxg5MSK0HEONwHP_skuXCHg2Jp4ZTiE29zMCCMbXf_f4sWJosCjjUxovCN2DLUJeoHe7GeLDO2dVlxznFLtBtu9RkXrlu6QS5QQmAvHzeNQezkRpiTCn5_Pox_CZTkrzkxXPKk_ItHsoRQU6UNb8eAAj6Vm2tBpP0lnDmHT1hL_fhFziKrMu_b_98m2L9sIpuF4WZxLLHK_pEMAhnDsvDqoy8NU5skIkpnqxj0t-BbMc3mAqyEqTknbimcTqRzJUxsdixwWTZrsGiLV2AegIPWWbpilOhf-uhEdwkHYtzNJSy3ThE2RpXKjSdCnuhgUa0rXfBJoJXeJTZ9tIaVxlV)

# Задание 4. Создание и документирование API

### 1. Тип API

Укажите, какой тип API вы будете использовать для взаимодействия микросервисов. Объясните своё решение.
AsyncAPI 

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