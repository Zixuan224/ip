# SillyRat User Guide

<img width="413" height="629" alt="1-129eaec6" src="https://github.com/user-attachments/assets/9fcd6af0-5c7c-485b-be60-339d00a389cf" />


**SillyRat** is your loyal (if slightly cheese-obsessed) task management chatbot. 🐀

Need to keep track of todos, deadlines, and events? SillyRat scurries through your task list so you don't have to. It remembers everything, saves your tasks automatically, and even reminds you about upcoming deadlines when you launch the app.

SillyRat features:
- ⚡ **Lightning-fast CLI-style commands** with a friendly GUI
- 📋 **Three task types**: Todos, Deadlines, and Events
- 🔍 **Search** across all your tasks
- 🔔 **Automatic reminders** for tasks due in the next 7 days
- 💾 **Persistent storage** — your tasks survive between sessions

---

## Quick Start

1. Ensure you have **Java 17** or above installed.
2. Download the latest `sillyrat.jar` from the releases.
3. Run `java -jar sillyrat.jar`.
4. Start typing commands — SillyRat is ready to serve!

---

## Features

## Adding todos

Adds a simple task with no date attached.

Format: `todo DESCRIPTION`

Example: `todo borrow cheese from library`

## Adding deadlines

Adds a task that needs to be completed before a specific date and time.

Format: `deadline DESCRIPTION /by YYYY-MM-DD HHmm`

Example: `deadline return book /by 2026-03-15 1800`

A new deadline task is added to your list with the specified due date.

```
Got it. I've added this task:
  [D][ ] return book (by: Mar 15 2026 1800)
Now you have 5 tasks in the list.
```

## Adding events

Adds a task that needs to be completed before a specific date and time.

Format: `event DESCRIPTION /from YYYY-MM-DD HHmm /to HHmm`

Example: `deadline return book /by 2026-03-15 1800`

A new deadline task is added to your list with the specified due date.

```
Got it. I've added this task:
  [D][ ] return book (by: Mar 15 2026 1800)
Now you have 5 tasks in the list.
```

## Searching tasks

Adds a task that needs to be completed before a specific date and time.

Format: `find KEYWORD`

Example: `find cheese`

SillyRat filters your current list and displays only the tasks matching your search term.

```
Here are the matching tasks in your list:
1.[T][ ] borrow cheese from library
```

## Listing all tasks

Displays all tasks currently stored in your list, showing their status, type, and any associated dates.

Format: `list`

Example: `list`

SillyRat provides a numbered list of all your current tasks.

```
Here are the tasks in your list:
1.[T][X] borrow cheese from library
2.[D][ ] return book (by: Mar 15 2026 18:00)
3.[E][ ] project meeting (from: Feb 25 2026 14:00 to: 16:00)
```

## Marking a task as done

Marks a specific task as completed by its index number.

Format: `mark INDEX`

Example: `mark 2`

The status of the task at the specified index is updated to "Done", indicated by an [X].
```
Nice! I've marked this task as done:
  [D][X] return book (by: Mar 15 2026 18:00)
```

## Marking a task as done

Marks a specific task as completed by its index number.

Format: `mark INDEX`

Example: `mark 2`

The status of the task at the specified index is updated to "Done", indicated by an [X].
```
Nice! I've marked this task as done:
  [D][X] return book (by: Mar 15 2026 18:00)
```

## Unmarking a task 

Reverts a completed task back to an incomplete status.

Format: `unmark INDEX`

Example: `unmark 2`

The status icon for the task at the specified index changes back to an empty box [ ].
```
OK, I've marked this task as not done yet:
  [D][ ] return book (by: Mar 15 2026 18:00)
```

## Deleting a task

Removes a task entirely from your list using its index number.

Format: `delete INDEX`

Example: `delete 1`

The task is permanently removed, and the total task count is updated.
```
Noted. I've removed this task:
  [T][X] borrow cheese from library
Now you have 2 tasks in the list.
```

## Getting reminders

Scans your list for any deadlines or events occurring within the next 7 days. 
This feature triggers **automatically** upon launching the application to ensure you never miss a deadline.

Format: `remind`

Example: `remind`

SillyRat identifies upcoming tasks to help you stay ahead of your schedule.
```
Squeak! Reminder — these tasks are due in the next
1. [E][ ] project meeting (from: Feb 25 2026 14:00 to: 16:00)
```

## Persistent Storage

All tasks are automatically saved to your hard drive whenever you make a change.

Your tasks survive between sessions; when you close and restart SillyRat, all your Todos, Deadlines, and Events are reloaded exactly as you left them.

## Exiting the program

Closes the application and ensures all session data is safely stored.

Format: `bye`

Example: `bye`

The GUI window closes after displaying a farewell message.
```
See you! Please bring more food next time :)
```
