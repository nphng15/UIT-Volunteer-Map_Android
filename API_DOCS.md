# Backend API Docs

This document describes the current backend API behavior in `packages/backend/src`.

## Base Information

- Local base URL: `http://localhost:5000/api`
- Content type: `application/json`
- Auth header for protected endpoints:

```http
Authorization: Bearer <jwt_token>
```

## Roles

- `admin`
- `leader`

## Common Response Format

Most endpoints use this response envelope:

```json
{
  "success": true,
  "data": {},
  "message": "Optional message"
}
```

Error responses are usually:

```json
{
  "success": false,
  "error": "Validation failed",
  "message": "field: detail"
}
```

## Important Notes For Mobile Team

- `GET /teams` is public and currently returns the guest-friendly shape only.
- `GET /posts/:id` does not return the same shape as `GET /posts`. It returns the raw post entity only.
- `GET /accounts` and `GET /accounts/:id` do not use the standard `{ success, data }` envelope.
- `GET /verify` always responds with HTTP `200`. You must check `success` and `data.isExpired`.
- User profile fields use PascalCase keys like `FullName`, `Email`, `PhoneNumber`.

## Authentication

### POST `/auth/login`

Brief: Login with username and password, receive JWT token and basic account info.

- Auth: Public
- Body:

```json
{
  "username": "leader01",
  "password": "leader123"
}
```

- Validation:
  - `username`: required string
  - `password`: required string

- Success response `200`:

```json
{
  "success": true,
  "data": {
    "token": "jwt_token_here",
    "user": {
      "accId": 12,
      "username": "leader01",
      "role": "leader"
    }
  }
}
```

- Common errors:
  - `400`: validation failed
  - `401`: invalid username or password
  - `500`: JWT secret missing on server

### POST `/auth/logout`

Brief: Client-side logout acknowledgement. This endpoint does not invalidate the JWT on the server.

- Auth: Public
- Body: none
- Success response `200`:

```json
{
  "success": true,
  "message": "Logged out successfully"
}
```

### GET `/verify`

Brief: Check whether a JWT is still valid or already expired.

- Auth: Optional bearer token
- Header:

```http
Authorization: Bearer <jwt_token>
```

- Success response when token is valid `200`:

```json
{
  "success": true,
  "data": {
    "isExpired": false
  }
}
```

- Response when token is invalid or expired `200`:

```json
{
  "success": true,
  "data": {
    "isExpired": true
  }
}
```

- Response when token is missing `200`:

```json
{
  "success": false,
  "error": "No token provided"
}
```

## Health

### GET `/health`

Brief: Basic health check for backend availability.

- Auth: Public
- Body: none
- Success response `200`:

```json
{
  "success": true,
  "data": {
    "status": "healthy",
    "timestamp": "2026-03-27T09:00:00.000Z",
    "uptime": 123.456
  },
  "message": "Server is running"
}
```

## User Profile

### GET `/users/profile`

Brief: Get the logged-in admin or leader profile.

- Auth: `admin`, `leader`
- Body: none
- Success response `200`:

```json
{
  "success": true,
  "data": {
    "UserId": "15",
    "FullName": "Nguyen Van A",
    "Mssv": "22520001",
    "Class": "SE123",
    "Email": "student@gm.uit.edu.vn",
    "PhoneNumber": "0912345678",
    "created_at": "2026-03-01 09:30:00"
  }
}
```

- Common errors:
  - `401`: token missing or expired
  - `403`: invalid token or wrong role
  - `404`: user not found

### PUT `/users/profile`

Brief: Update the logged-in admin or leader profile.

- Auth: `admin`, `leader`
- Allowed body fields only:

```json
{
  "FullName": "Nguyen Van B",
  "Mssv": "22520001",
  "Class": "SE123",
  "Email": "student-new@gm.uit.edu.vn",
  "PhoneNumber": "0988888888"
}
```

- Validation:
  - all fields are optional
  - `Email` must be valid email format
  - `PhoneNumber` must be at least 10 characters
  - unknown fields are rejected
  - `userId`, `UserId`, `role` are forbidden

- Success response `200`:

```json
{
  "success": true,
  "data": {
    "UserId": "15",
    "FullName": "Nguyen Van B",
    "Mssv": "22520001",
    "Class": "SE123",
    "Email": "student-new@gm.uit.edu.vn",
    "PhoneNumber": "0988888888",
    "created_at": "2026-03-01 09:30:00"
  }
}
```

- Common errors:
  - `400`: validation failed
  - `403`: forbidden field in body
  - `409`: email already taken

## Campaigns

All campaign endpoints require authentication and allow `admin` or `leader`.

### GET `/campaigns`

Brief: Get all campaigns.

- Auth: `admin`, `leader`
- Body: none
- Success response `200`:

```json
{
  "success": true,
  "data": [
    {
      "campaignId": 1,
      "campaignName": "Mua He Xanh 2026",
      "description": "Volunteer campaign",
      "startDate": "2026-06-01",
      "endDate": "2026-07-15"
    }
  ]
}
```

### GET `/campaigns/:id`

Brief: Get one campaign by `campaignId`.

- Auth: `admin`, `leader`
- Path params:
  - `id`: number
- Success response `200`:

```json
{
  "success": true,
  "data": {
    "campaignId": 1,
    "campaignName": "Mua He Xanh 2026",
    "description": "Volunteer campaign",
    "startDate": "2026-06-01",
    "endDate": "2026-07-15"
  }
}
```

- Common errors:
  - `400`: invalid campaign ID
  - `404`: campaign not found

### POST `/campaigns`

Brief: Create a new campaign.

- Auth: `admin`, `leader`
- Body:

```json
{
  "campaignName": "Mua He Xanh 2026",
  "description": "Volunteer campaign",
  "startDate": "2026-06-01",
  "endDate": "2026-07-15"
}
```

- Validation:
  - `campaignName`: required string
  - `startDate`: required string
  - `endDate`: required string
  - `description`: optional string or `null`

- Success response `201`:

```json
{
  "success": true,
  "data": {
    "campaignId": 1,
    "campaignName": "Mua He Xanh 2026",
    "description": "Volunteer campaign",
    "startDate": "2026-06-01",
    "endDate": "2026-07-15"
  }
}
```

- Common errors:
  - `400`: validation failed
  - `409`: campaign name already exists

### PUT `/campaigns/:id`

Brief: Update an existing campaign. Partial update is supported.

- Auth: `admin`, `leader`
- Path params:
  - `id`: number
- Body example:

```json
{
  "description": "Updated description"
}
```

- Success response `200`:

```json
{
  "success": true,
  "data": {
    "campaignId": 1,
    "campaignName": "Mua He Xanh 2026",
    "description": "Updated description",
    "startDate": "2026-06-01",
    "endDate": "2026-07-15"
  }
}
```

### DELETE `/campaigns/:id`

Brief: Delete a campaign permanently.

- Auth: `admin`, `leader`
- Path params:
  - `id`: number
- Success response `200`:

```json
{
  "success": true,
  "message": "Campaign deleted successfully"
}
```

## Teams

### GET `/teams`

Brief: Get team list for public/guest usage.

- Auth: Public
- Body: none
- Current response shape `200`:

```json
{
  "success": true,
  "data": [
    {
      "teamId": 3,
      "teamName": "Media Team",
      "imageUrl": "https://example.com/team.jpg",
      "leaders": [
        {
          "userId": 20,
          "fullName": "Tran Thi B",
          "role": "leader"
        }
      ]
    }
  ]
}
```

- Note: because this route is not protected, it currently does not return member details.

### GET `/teams/:id`

Brief: Get one team with leader information.

- Auth: Public
- Path params:
  - `id`: number
- Success response `200`:

```json
{
  "success": true,
  "data": {
    "teamId": 3,
    "teamName": "Media Team",
    "description": "Support media activities",
    "imageUrl": "https://example.com/team.jpg",
    "leaders": [
      {
        "userId": 20,
        "fullName": "Tran Thi B",
        "role": "leader",
        "avatarUrl": "https://example.com/avatar.jpg"
      }
    ]
  }
}
```

### POST `/teams`

Brief: Create a new team.

- Auth: `admin`
- Body:

```json
{
  "teamName": "Media Team",
  "leaderId": 20,
  "campaignId": 1,
  "description": "Support media activities",
  "imageUrl": "https://example.com/team.jpg",
  "attachments": [
    {
      "imageUrl": "https://example.com/a1.jpg",
      "position": 0
    }
  ]
}
```

- Validation:
  - `teamName`: required, 3 to 100 chars
  - `leaderId`: required positive integer
  - `campaignId`: required positive integer
  - `description`: optional string or `null`
  - `imageUrl`: optional valid URL or `null`
  - `attachments`: optional array
  - each attachment:
    - `imageUrl`: required valid URL
    - `position`: optional integer, minimum `0`

- Success response `201`:

```json
{
  "success": true,
  "message": "Team created successfully",
  "data": {
    "teamId": 3,
    "teamName": "Media Team",
    "description": "Support media activities",
    "isDeleted": 0,
    "imageUrl": "https://example.com/team.jpg",
    "leader": {
      "userId": 20,
      "fullName": "Tran Thi B"
    },
    "attachments": [
      {
        "attachmentId": 1,
        "imageUrl": "https://example.com/a1.jpg",
        "uploadedAt": "2026-03-27T09:00:00.000Z",
        "position": 0
      }
    ]
  }
}
```

- Common errors:
  - `400`: validation failed, invalid leader role, campaign not found
  - `404`: leader not found
  - `409`: team name already exists in the campaign

### PUT `/teams/:id`

Brief: Update team basic information.

- Auth: `admin`, `leader`
- Leader rule: a leader can only update their own team
- Body:

```json
{
  "teamName": "Media Team Updated",
  "description": "Updated description",
  "imageUrl": "https://example.com/team-new.jpg"
}
```

- Validation:
  - all fields optional
  - `teamName`: non-empty string if provided
  - `imageUrl`: valid URL if provided

- Success response `200`:

```json
{
  "success": true,
  "message": "Team updated successfully",
  "data": {
    "teamId": 3,
    "teamName": "Media Team Updated",
    "description": "Updated description",
    "isDeleted": 0,
    "imageUrl": "https://example.com/team-new.jpg"
  }
}
```

- Common errors:
  - `403`: leader tries to update another team
  - `404`: team not found

### DELETE `/teams/:id`

Brief: Soft delete a team. Related users in that team are also soft deleted.

- Auth: `admin`
- Path params:
  - `id`: number
- Success response `200`:

```json
{
  "success": true,
  "message": "Team deleted successfully"
}
```

### POST `/teams/:id/attachments`

Brief: Add one or more gallery images to a team.

- Auth: `admin`, `leader`
- Leader rule: a leader can only add attachments to their own team
- Path params:
  - `id`: number
- Body:

```json
{
  "attachments": [
    {
      "imageUrl": "https://example.com/a2.jpg",
      "position": 1
    },
    {
      "imageUrl": "https://example.com/a3.jpg"
    }
  ]
}
```

- Validation:
  - `attachments`: required array, at least 1 item
  - each item:
    - `imageUrl`: required valid URL
    - `position`: optional integer, minimum `0`

- Success response `201`:

```json
{
  "success": true,
  "message": "Attachments added successfully",
  "data": [
    {
      "attachmentId": 2,
      "imageUrl": "https://example.com/a2.jpg",
      "uploadedAt": "2026-03-27T09:00:00.000Z",
      "position": 1
    }
  ]
}
```

### GET `/teams/:id/attachments`

Brief: Get one team together with its attachment list.

- Auth: Public
- Path params:
  - `id`: number
- Success response `200`:

```json
{
  "success": true,
  "data": {
    "teamId": 3,
    "teamName": "Media Team",
    "description": "Support media activities",
    "isDeleted": 0,
    "imageUrl": "https://example.com/team.jpg",
    "leader": {
      "userId": 20,
      "fullName": "Tran Thi B",
      "avatarUrl": "https://example.com/avatar.jpg"
    },
    "attachments": [
      {
        "attachmentId": 1,
        "imageUrl": "https://example.com/a1.jpg",
        "uploadedAt": "2026-03-27T09:00:00.000Z",
        "position": 0
      }
    ]
  }
}
```

- Note: this endpoint returns a raw team entity with nested relations, so it may include more fields than the public team list endpoint.

## Posts

### GET `/posts`

Brief: Get all public posts that are not soft deleted.

- Auth: Public
- Body: none
- Success response `200`:

```json
{
  "success": true,
  "data": [
    {
      "postId": 11,
      "title": "Campaign Kickoff",
      "content": "Opening event details",
      "createdAt": "2026-03-20T08:00:00.000Z",
      "updatedAt": "2026-03-20T08:00:00.000Z",
      "thumbnail": {
        "photoId": 100,
        "imageUrl": "https://example.com/post-thumb.jpg",
        "title": "Cover"
      },
      "team": {
        "teamId": 3,
        "teamName": "Media Team"
      },
      "author": {
        "userId": 20,
        "fullName": "Tran Thi B"
      }
    }
  ]
}
```

### GET `/posts/:id`

Brief: Get a single post by `postId`.

- Auth: Public
- Path params:
  - `id`: number
- Current response shape `200`:

```json
{
  "success": true,
  "data": {
    "postId": 11,
    "title": "Campaign Kickoff",
    "content": "Opening event details",
    "isDeleted": 0,
    "createdAt": "2026-03-20T08:00:00.000Z",
    "updatedAt": "2026-03-20T08:00:00.000Z"
  }
}
```

- Note: this endpoint currently returns the raw post entity, not the richer list item shape from `GET /posts`.

### POST `/posts`

Brief: Create a new post, optionally with initial photos.

- Auth: `admin`, `leader`
- Body:

```json
{
  "title": "Campaign Kickoff",
  "content": "Opening event details",
  "teamId": 3,
  "authorId": 20,
  "photos": [
    {
      "title": "Cover",
      "imageUrl": "https://example.com/post-1.jpg",
      "isFirstImage": 1
    }
  ]
}
```

- Validation:
  - `title`: required string
  - `content`: required string
  - `teamId`: required number
  - `authorId`: required number
  - `photos`: optional array
  - each photo:
    - `title`: optional string or `null`
    - `imageUrl`: required valid URL
    - `isFirstImage`: optional integer `0` or `1`

- Success response `200`:

```json
{
  "success": true,
  "message": "Post created successfully",
  "data": {
    "postId": 11,
    "title": "Campaign Kickoff",
    "content": "Opening event details",
    "isDeleted": 0,
    "createdAt": "2026-03-27T09:00:00.000Z",
    "updatedAt": "2026-03-27T09:00:00.000Z",
    "photos": [
      {
        "photoId": 100,
        "title": "Cover",
        "imageUrl": "https://example.com/post-1.jpg",
        "uploadedAt": "2026-03-27T09:00:00.000Z",
        "isFirstImage": 1,
        "isDeleted": 0
      }
    ]
  }
}
```

### POST `/posts/:id/photos`

Brief: Add one photo to an existing post.

- Auth: `admin`, `leader`
- Path params:
  - `id`: number
- Body:

```json
{
  "title": "Extra Image",
  "imageUrl": "https://example.com/post-2.jpg",
  "isFirstImage": 0
}
```

- Validation:
  - `imageUrl`: required valid URL
  - `title`: optional string or `null`
  - `isFirstImage`: optional integer

- Success response `200`:

```json
{
  "success": true,
  "data": {
    "photoId": 101,
    "title": "Extra Image",
    "imageUrl": "https://example.com/post-2.jpg",
    "uploadedAt": "2026-03-27T09:00:00.000Z",
    "isFirstImage": 0,
    "isDeleted": 0
  }
}
```

- Note: when `id` is not numeric, the current backend returns `400` with error `"Post not found"`.

### PUT `/posts/:id`

Brief: Update basic post information. Partial update is supported.

- Auth: `admin`, `leader`
- Path params:
  - `id`: number
- Body:

```json
{
  "title": "Campaign Kickoff Updated",
  "content": "Updated details",
  "teamId": 3,
  "authorId": 20
}
```

- Success response `200`:

```json
{
  "success": true,
  "message": "Post updated successfully",
  "data": {
    "postId": 11,
    "title": "Campaign Kickoff Updated",
    "content": "Updated details",
    "isDeleted": 0,
    "createdAt": "2026-03-27T09:00:00.000Z",
    "updatedAt": "2026-03-27T09:10:00.000Z"
  }
}
```

### DELETE `/posts/:id`

Brief: Soft delete a post.

- Auth: `admin`, `leader`
- Path params:
  - `id`: number
- Success response `200`:

```json
{
  "success": true,
  "message": "Post deleted successfully",
  "data": {
    "postId": 11,
    "title": "Campaign Kickoff Updated",
    "content": "Updated details",
    "isDeleted": 1,
    "createdAt": "2026-03-27T09:00:00.000Z",
    "updatedAt": "2026-03-27T09:10:00.000Z"
  }
}
```

## Accounts

All account endpoints require `admin`.

These endpoints are mainly for admin management, not typical mobile-user flows.

### GET `/accounts`

Brief: Get all non-deleted accounts.

- Auth: `admin`
- Success response `200`:

```json
[
  {
    "accId": 12,
    "username": "leader01",
    "createdAt": "2026-03-01 09:30:00",
    "roleName": "leader"
  }
]
```

- Note: this endpoint returns a raw array, not `{ success, data }`.

### POST `/accounts`

Brief: Create a new leader account and the linked user record.

- Auth: `admin`
- Body:

```json
{
  "fullname": "Tran Thi B",
  "mssv": "22520001",
  "class": "SE123",
  "email": "22520001@gm.uit.edu.vn",
  "teamId": 3,
  "phoneNumber": "0912345678",
  "username": "leader01",
  "password": "leader123"
}
```

- Validation:
  - `fullname`: minimum 3 chars
  - `mssv`: exactly 8 chars
  - `class`: maximum 15 chars
  - `email`: valid email and must end with `@gm.uit.edu.vn`
  - `teamId`: number
  - `phoneNumber`: minimum 10 chars
  - `username`: minimum 3 chars
  - `password`: minimum 6 chars

- Success response `201`:

```json
{
  "success": true,
  "message": "Tạo tài khoản thành công"
}
```

- Common errors:
  - `400`: username exists, team not found, validation failed

### PUT `/accounts/:id`

Brief: Update password and/or role of an account.

- Auth: `admin`
- Path params:
  - `id`: number
- Body:

```json
{
  "password": "newPassword123",
  "roleId": "leader"
}
```

- Validation:
  - `password`: optional, minimum 6 chars
  - `roleId`: optional, must be `admin` or `leader`

- Success response `200`:

```json
{
  "success": true,
  "data": {
    "accId": 12,
    "username": "leader01",
    "createdAt": "2026-03-01T09:30:00.000Z",
    "updatedAt": "2026-03-27 09:30:00",
    "roleName": "leader"
  },
  "message": "Cập nhật tài khoản thành công"
}
```

### DELETE `/accounts/:id`

Brief: Soft delete an account.

- Auth: `admin`
- Path params:
  - `id`: number
- Success response `200`:

```json
{
  "success": true,
  "message": "Xóa tài khoản thành công"
}
```

### GET `/accounts/:id`

Brief: Get raw account information by `accId`.

- Auth: `admin`
- Path params:
  - `id`: number
- Current response shape `200`:

```json
{
  "accId": 12,
  "username": "leader01",
  "password": "$2b$10$...",
  "roleId": 2,
  "createdAt": "2026-03-01T09:30:00.000Z",
  "updatedAt": null,
  "isDeleted": false
}
```

- Note: this endpoint returns the raw account entity and currently includes internal fields. It is better treated as an admin/internal endpoint only.

## Role Test Endpoints

These are simple permission-check endpoints.

### GET `/admin/admin-only`

Brief: Verify that the current token belongs to an admin.

- Auth: `admin`
- Success response `200`:

```json
{
  "success": true,
  "message": "Admin access granted"
}
```

### GET `/leader/leader-only`

Brief: Verify that the current token belongs to a leader.

- Auth: `leader`
- Success response `200`:

```json
{
  "success": true,
  "message": "Leader access granted"
}
```

## Common Auth Errors

Protected endpoints may return:

```json
{
  "success": false,
  "error": "Access token required"
}
```

```json
{
  "success": false,
  "error": "Token has expired"
}
```

```json
{
  "success": false,
  "error": "Invalid or expired token"
}
```

```json
{
  "success": false,
  "error": "Permission denied"
}
```
