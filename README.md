# VKMessanger

This application is a simple mobile client for social network **VKontakte**

##Requirements
> API Level 15 (Android 4.0.3 Ice Cream Sandwich)

##Screenshots
![1](https://cloud.githubusercontent.com/assets/10827392/15745659/b7ae3118-28db-11e6-8972-7a6568060679.jpg)
![2](https://cloud.githubusercontent.com/assets/10827392/15745661/b7b4ccee-28db-11e6-9fe7-2fb0704e3a61.jpg)
![3](https://cloud.githubusercontent.com/assets/10827392/15745660/b7b45eee-28db-11e6-9853-21836a34b405.jpg)
![4](https://cloud.githubusercontent.com/assets/10827392/15745662/b7b6bd56-28db-11e6-9d21-86b405611376.jpg)
![5](https://cloud.githubusercontent.com/assets/10827392/15745664/b7ba5272-28db-11e6-9e0b-c6541aedc5d1.jpg)
![6](https://cloud.githubusercontent.com/assets/10827392/15745663/b7b83794-28db-11e6-99a3-8e2e765ff164.jpg)

##Правила работы с репозиторием:

Перед началом работы с репозиторием каждый раз необходимо обновить проект с помощью меню:
>VCS -> Update Project

Получить актуальные данные с интересующей ветки можно с помощью меню:
>VCS -> Git -> Pull

###Master branch
В ветку **'master'** ничего нельзя **коммитить**, а также производить **слияние** без предварительного согласования 
с другими членами комманды.

###Наименование веток

#####Разработка
Ветки в которых происходит разработка должны иметь имя **'develop-XXXXX'**

#####Добавление нового функционала
В случае, когда надо добавить новый функционал к существующей части проекта, необходимо
создать отдельную ветку **'feature-XXXXX'**, в которой и производить изменения кода, после 
чего слить ее в соответствующую ветку 'develop-XXXXX'

#####Исправление багов
Все исправления найденных ошибок, которые уже были закоммичены ранее,
необходимо проводить в отдельных ветках **'hotfix-XXXXX'**
