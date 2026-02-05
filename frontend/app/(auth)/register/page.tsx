"use client"

import React from "react"

import { useState } from "react"
import Link from "next/link"
import { useRouter } from "next/navigation"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Checkbox } from "@/components/ui/checkbox"
import { registerUser } from "@/lib/auth"
import { Eye, EyeOff, Loader2, Check } from "lucide-react"

export default function RegisterPage() {
  const router = useRouter()
  const [username, setUsername] = useState("")
  const [email, setEmail] = useState("")
  const [password, setPassword] = useState("")
  const [confirmPassword, setConfirmPassword] = useState("")
  const [showPassword, setShowPassword] = useState(false)
  const [showConfirmPassword, setShowConfirmPassword] = useState(false)
  const [acceptTerms, setAcceptTerms] = useState(false)
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState("")
  const [success, setSuccess] = useState(false)

  const passwordRequirements = [
    { label: "At least 8 characters", met: password.length >= 8 },
    { label: "Contains a number", met: /\d/.test(password) },
    { label: "Contains uppercase letter", met: /[A-Z]/.test(password) },
    { label: "Passwords match", met: password === confirmPassword && password.length > 0 },
  ]

  const isPasswordValid = passwordRequirements.every((req) => req.met)

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError("")

    if (!isPasswordValid) {
      setError("Please meet all password requirements")
      return
    }

    if (!acceptTerms) {
      setError("Please accept the terms and conditions")
      return
    }

    setIsLoading(true)

    // Check if username and email are available
    const { checkCredentialAvailability } = await import("@/lib/auth")
    const availability = await checkCredentialAvailability(username, email)

    if (!availability) {
      setError("Failed to verify credentials. Please try again.")
      setIsLoading(false)
      return
    }

    if (!availability.usernameAvailable || !availability.emailAvailable) {
      setError(availability.message)
      setIsLoading(false)
      return
    }

    // Store credentials for onboarding
    if (typeof window !== "undefined") {
      sessionStorage.setItem("registration_username", username)
      sessionStorage.setItem("registration_email", email)
      sessionStorage.setItem("registration_password", password)
    }

    // Redirect to onboarding
    router.push("/onboarding")
    setIsLoading(false)
  }

  return (
    <div className="w-full max-w-md space-y-8">
      <div className="text-center">
        <h1 className="text-3xl font-bold tracking-tight text-foreground">Create your account</h1>
        <p className="mt-2 text-muted-foreground">
          Start your journey to a healthier lifestyle
        </p>
      </div>

      <form onSubmit={handleSubmit} className="space-y-5">
        {error && (
          <div className="rounded-lg border border-destructive/50 bg-destructive/10 p-4 text-sm text-destructive">
            {error}
          </div>
        )}

        <div className="space-y-2">
          <Label htmlFor="username">Username</Label>
          <Input
            id="username"
            type="text"
            placeholder="johndoe"
            value={username}
            onChange={(e: React.ChangeEvent<HTMLInputElement>) => setUsername(e.target.value)}
            required
            className="h-12"
          />
        </div>

        <div className="space-y-2">
          <Label htmlFor="email">Email</Label>
          <Input
            id="email"
            type="email"
            placeholder="you@example.com"
            value={email}
            onChange={(e: React.ChangeEvent<HTMLInputElement>) => setEmail(e.target.value)}
            required
            className="h-12"
          />
        </div>

        <div className="space-y-2">
          <Label htmlFor="password">Password</Label>
          <div className="relative">
            <Input
              id="password"
              type={showPassword ? "text" : "password"}
              placeholder="Create a strong password"
              value={password}
              onChange={(e: React.ChangeEvent<HTMLInputElement>) => setPassword(e.target.value)}
              required
              className="h-12 pr-12"
            />
            <button
              type="button"
              onClick={() => setShowPassword(!showPassword)}
              className="absolute right-4 top-1/2 -translate-y-1/2 text-muted-foreground hover:text-foreground"
              aria-label={showPassword ? "Hide password" : "Show password"}
            >
              {showPassword ? <EyeOff className="h-5 w-5" /> : <Eye className="h-5 w-5" />}
            </button>
          </div>
        </div>

        <div className="space-y-2">
          <Label htmlFor="confirmPassword">Confirm Password</Label>
          <div className="relative">
            <Input
              id="confirmPassword"
              type={showConfirmPassword ? "text" : "password"}
              placeholder="Confirm your password"
              value={confirmPassword}
              onChange={(e: React.ChangeEvent<HTMLInputElement>) => setConfirmPassword(e.target.value)}
              required
              className="h-12 pr-12"
            />
            <button
              type="button"
              onClick={() => setShowConfirmPassword(!showConfirmPassword)}
              className="absolute right-4 top-1/2 -translate-y-1/2 text-muted-foreground hover:text-foreground"
              aria-label={showConfirmPassword ? "Hide password" : "Show password"}
            >
              {showConfirmPassword ? <EyeOff className="h-5 w-5" /> : <Eye className="h-5 w-5" />}
            </button>
          </div>
        </div>

        {password.length > 0 && (
          <div className="space-y-2 rounded-lg bg-muted/50 p-4">
            <p className="text-xs font-medium text-muted-foreground">Password requirements:</p>
            <ul className="grid grid-cols-2 gap-2">
              {passwordRequirements.map((req) => (
                <li 
                  key={req.label} 
                  className={`flex items-center gap-2 text-xs ${req.met ? 'text-secondary' : 'text-muted-foreground'}`}
                >
                  <div className={`flex h-4 w-4 items-center justify-center rounded-full ${req.met ? 'bg-secondary/20' : 'bg-muted'}`}>
                    {req.met && <Check className="h-3 w-3" />}
                  </div>
                  {req.label}
                </li>
              ))}
            </ul>
          </div>
        )}

        <div className="flex items-start gap-3">
          <Checkbox
            id="terms"
            checked={acceptTerms}
            onCheckedChange={(checked: boolean) => setAcceptTerms(checked)}
            className="mt-0.5"
          />
          <Label htmlFor="terms" className="text-sm font-normal text-muted-foreground">
            {"I agree to the "}
            <Link href="#" className="font-medium text-secondary hover:text-secondary/80">
              Terms of Service
            </Link>
            {" and "}
            <Link href="#" className="font-medium text-secondary hover:text-secondary/80">
              Privacy Policy
            </Link>
          </Label>
        </div>

        <Button 
          type="submit" 
          className="h-12 w-full bg-primary text-primary-foreground hover:bg-primary/90"
          disabled={isLoading || !acceptTerms}
        >
          {isLoading ? (
            <>
              <Loader2 className="mr-2 h-4 w-4 animate-spin" />
              Creating account...
            </>
          ) : (
            "Create account"
          )}
        </Button>
      </form>

      <p className="text-center text-sm text-muted-foreground">
        Already have an account?{" "}
        <Link href="/login" className="font-semibold text-secondary hover:text-secondary/80">
          Sign in
        </Link>
      </p>
    </div>
  )
}
