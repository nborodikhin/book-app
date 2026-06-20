# Bookshelf

A lightweight Android app for discovering books and maintaining a personal reading list, powered by the [OpenLibrary](https://openlibrary.org) public API. No account required — all personal data stays on device.

https://github.com/user-attachments/assets/48a097f8-ddf0-464d-99b5-707d772122cd

## Features

**Search tab**
- Search books by title or author against the OpenLibrary database
- Results show cover image, author, title, and a bookmark toggle
- Search triggers automatically after 2 seconds of inactivity
- Results paginate as you scroll; pages are cached in memory for the session
- Loading and error states with retry prompt

**Bookmarks tab**
- Offline list of all bookmarked books, stored locally in Room DB
- Tap any book to open its detail screen

**Book detail screen**
- Cover image, author, title, and synopsis
- Bookmark toggle — bookmarking a book fetches and stores its data locally once, permanently
- Editable personal note (bookmarked books only), limited to 300 characters with a live counter

## Tech Stack

| Layer | Library |
|---|---|
| UI | Jetpack Compose + Material 3 |
| Navigation | Compose Navigation |
| Image loading | Coil |
| Networking | Retrofit + kotlinx.serialization |
| Local DB | Room |
| Preferences | DataStore |
| DI | Hilt |

## Data & Storage

- **Room DB** — bookmarked book data (title, authors, synopsis, cover URL). Written once on first bookmark, never deleted.
- **DataStore** — bookmark flags and per-book notes (max 300 chars).
- **In-memory cache** — search results keyed by `query:page`, lives for the app process lifetime.

## OpenLibrary APIs Used

| Purpose | Endpoint |
|---|---|
| Search | `GET https://openlibrary.org/search.json?q={query}&page={page}` |
| Work detail | `GET https://openlibrary.org/works/{id}.json` |
| Cover image | `https://covers.openlibrary.org/b/id/{cover_id}-M.jpg` |

## Requirements

- Android 7.0+ (minSdk 24)
- Internet connection for search and initial book data fetch; bookmarked content works offline
