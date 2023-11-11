# README

## Sequence Generation

Sequences in this application are generated sequentially in alphabetical order, starting from 'abc' to 'abd', 'abe', again up to 'abz', after 'abz', it proceeds from 'aca'. The latest sequence ('last_sequence') is used as the starting point for the next generation. In case your current latest sequence is alphabetically less than the current value of a file, you have the option to change it and use it as the starting point.

> ⚠️  Every URL sequence must be unique.

The 'last_generated_amount' automatically increases when new links are generated. It is not recommended to manually edit this value.

Links are auto-generated every 12 hours if the program decides that the remaining links are insufficient for the current rate of new messages.

For setting up the SQL tables used in this application to be compatible with the current version, use schema.sql.

## Usage Guide

### MessageMapping

`MessageMapping` is a Controller class that handles operations related to messages. Here are the URI mappings:

#### GetMessageByValue

Get a message by its value. A GET request to `/message/get/{value}` returns the message object with the specified value.

#### GetAllUsersMessages

A GET request to `/message/get/all` retrieves all the messages of a user. Available only for registered users

#### NewMessage

A GET request to `/message/new/{content}/{stringDeletionDate}` creates a new message that auto-expire in validity based on the `stringDeletionDate` value. Throws `NoAvailableShortURLException` if there are no short URLs currently available.

Also you can create a new messages with a from by GET request to `/message/new`
#### EditMessageContent

A GET request to `/message/edit/{value}/{content}` enables you to update an existing message's content based on its value.

#### EditMessageContentAndDeletionDate

A GET request to `/message/edit/{value}/{content}/{deletionDate}` allows you to update an existing message's content and deletion date.

#### EditMessageDeletionDate

A GET request to `/message/edit-time/{value}/{deletionDate}` enables you to update an existing message's deletion date.

#### DeleteMessage

A GET request to `/message/delete/{value}` softly deletes a message by its value.

### RootMapping

#### Register

A GET request to `/register` returns the registration view.

#### Login

A GET request to `/login` to log in.

#### Logout
A GET request to `/logout`

### UserMapping

#### Save

A POST request to `/user/save` saves a new user.

#### Login

A POST request to `/user/perform_login` logs in a user and throws `NoSuchElementException` if the credentials are incorrect. 

The user guides of each program are included in their respective explanations. Remember to replace "{value}", "{content}", "{stringDeletionDate}", and "{deletionDate}" with actual values when making requests.