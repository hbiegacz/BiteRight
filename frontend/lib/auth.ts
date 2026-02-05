const API_BASE_URL = (process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080").replace(/\/$/, "")

export interface AuthResponse {
  success: boolean
  message?: string
  token?: string
}

async function baseFetch(endpoint: string, options: RequestInit = {}) {
  const path = endpoint.startsWith("/") ? endpoint : `/${endpoint}`
  return fetch(`${API_BASE_URL}${path}`, options)
}

export async function registerUser(username: string, email: string, password: string): Promise<AuthResponse> {
  try {
    const response = await baseFetch("/api/auth/register", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ username, password, email }),
    })

    if (response.ok) {
      const message = await response.text()
      return { success: true, message }
    } else {
      const error = await response.text()
      return { success: false, message: error || "Registration failed" }
    }
  } catch (error) {
    return { success: false, message: "Network error. Please try again." }
  }
}

export interface OnboardingData {
  username: string
  email: string
  password: string
  name: string
  surname: string
  age: number
  weight: number
  height: number
  bmi: number
  lifestyle: string
  goalType: string
  goalWeight: number
  goalDate: string
  calorieLimit: number
  proteinLimit: number
  carbLimit: number
  fatLimit: number
  waterGoal: number
}

export async function registerWithOnboarding(data: OnboardingData): Promise<AuthResponse> {
  try {
    const response = await baseFetch("/api/auth/register", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(data),
    })

    if (response.ok) {
      const message = await response.text()
      return { success: true, message }
    } else {
      const error = await response.text()
      return { success: false, message: error || "Registration failed" }
    }
  } catch (error) {
    return { success: false, message: "Network error. Please try again." }
  }
}

export interface AvailabilityCheckResponse {
  usernameAvailable: boolean
  emailAvailable: boolean
  message: string
}

export async function checkCredentialAvailability(
  username: string,
  email: string
): Promise<AvailabilityCheckResponse | null> {
  try {
    const response = await baseFetch("/api/auth/check-availability", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ username, email }),
    })

    if (response.ok) {
      return await response.json()
    }
    return null
  } catch (error) {
    return null
  }
}

export async function loginUser(email: string, password: string): Promise<AuthResponse> {
  try {
    const response = await baseFetch("/api/auth/login", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ email, password }),
    })

    if (response.ok) {
      const token = await response.text()
      return { success: true, token }
    } else {
      const error = await response.text()
      return { success: false, message: error || "Login failed" }
    }
  } catch (error) {
    return { success: false, message: "Network error. Please try again." }
  }
}

export async function verifyUser(email: string, code: string): Promise<AuthResponse> {
  try {
    const response = await baseFetch("/api/auth/verifyuser", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ email, code }),
    })

    if (response.ok) {
      const message = await response.text()
      return { success: true, message }
    } else {
      const error = await response.text()
      return { success: false, message: error || "Verification failed" }
    }
  } catch (error) {
    return { success: false, message: "Network error. Please try again." }
  }
}

export async function forgotPassword(email: string): Promise<AuthResponse> {
  try {
    const response = await baseFetch(`/api/auth/forgottenpassword/${encodeURIComponent(email)}`, {
      method: "PUT",
    })

    if (response.ok) {
      const message = await response.text()
      return { success: true, message }
    } else {
      const error = await response.text()
      return { success: false, message: error || "Failed to send reset email" }
    }
  } catch (error) {
    return { success: false, message: "Network error. Please try again." }
  }
}

export async function resetForgottenPassword(
  email: string,
  code: string,
  newPassword: string,
): Promise<AuthResponse> {
  try {
    const response = await baseFetch("/api/auth/resetforgottenpassword", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ email, code, newPassword }),
    })

    if (response.ok) {
      const message = await response.text()
      return { success: true, message }
    } else {
      const error = await response.text()
      return { success: false, message: error || "Password reset failed" }
    }
  } catch (error) {
    return { success: false, message: "Network error. Please try again." }
  }
}

export function saveToken(token: string) {
  if (typeof window !== "undefined") {
    localStorage.setItem("biteright_token", token)
  }
}

export function getToken(): string | null {
  try {
    if (typeof window !== "undefined") {
      return localStorage.getItem("biteright_token")
    }
  } catch (error) {
    console.error("Error getting token:", error)
  }
  return null
}

export function removeToken() {
  if (typeof window !== "undefined") {
    localStorage.removeItem("biteright_token")
  }
}

export function isAuthenticated(): boolean {
  return !!getToken()
}

// Account management functions (authenticated)
async function authFetch(endpoint: string, options: RequestInit = {}) {
  const token = getToken()
  const headers: HeadersInit = {
    "Content-Type": "application/json",
    ...options.headers,
  }

  if (token) {
    ; (headers as Record<string, string>)["Authorization"] = `Bearer ${token}`
  }

  const path = endpoint.startsWith("/") ? endpoint : `/${endpoint}`
  const response = await fetch(`${API_BASE_URL}${path}`, { ...options, headers })

  if (response.status === 401 || response.status === 403) {
    if (typeof window !== "undefined") {
      removeToken()
      window.location.href = "/login"
    }
  }

  return response
}

export async function changeUsername(newUsername: string): Promise<AuthResponse> {
  try {
    const response = await authFetch("/api/auth/changeusername", {
      method: "POST",
      body: JSON.stringify({ newUsername }),
    })
    if (response.ok) {
      const message = await response.text()
      return { success: true, message }
    } else {
      const error = await response.text()
      return { success: false, message: error || "Failed to change username" }
    }
  } catch {
    return { success: false, message: "Network error. Please try again." }
  }
}

export async function changeEmail(newEmail: string): Promise<AuthResponse> {
  try {
    const response = await authFetch("/api/auth/changeemail", {
      method: "POST",
      body: JSON.stringify({ newEmail }),
    })
    if (response.ok) {
      const text = await response.text()
      saveToken(text)
      return { success: true, token: text }
    } else {
      const error = await response.text()
      return { success: false, message: error || "Failed to change email" }
    }
  } catch {
    return { success: false, message: "Network error. Please try again." }
  }
}

export async function changePassword(oldPassword: string, newPassword: string): Promise<AuthResponse> {
  try {
    const response = await authFetch("/api/auth/changepassword", {
      method: "POST",
      body: JSON.stringify({ oldPassword, newPassword }),
    })
    if (response.ok) {
      const message = await response.text()
      return { success: true, message }
    } else {
      const error = await response.text()
      return { success: false, message: error || "Failed to change password" }
    }
  } catch {
    return { success: false, message: "Network error. Please try again." }
  }
}
