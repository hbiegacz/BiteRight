"use client"

import { useState, useEffect } from "react"
import { useRouter } from "next/navigation"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Calendar } from "@/components/ui/calendar"
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover"
import { format, parseISO } from "date-fns"
import { cn } from "@/lib/utils"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Separator } from "@/components/ui/separator"
import { Switch } from "@/components/ui/switch"
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select"
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog"
import {
  getDailyLimits,
  updateDailyLimits,
  createDailyLimits,
  addWeight,
  getLastWeight,
  getCurrentUser,
  getUserInfo,
  updateUserInfo,
  getUserGoal,
  updateUserGoal,
  getUserPreferences,
  updateUserPreferences,
  type UserDTO,
  type UserInfoDTO,
  type UserGoalDTO,
  type UserPreferencesDTO,
} from "@/lib/api"
import { changeUsername, changeEmail, changePassword, removeToken } from "@/lib/auth"
import {
  Save,
  Target,
  Scale,
  User,
  Lock,
  Mail,
  Trash2,
  Bell,
  Palette,
  Globe,
  AlertTriangle,
  Loader2,
  Check,
  Eye,
  EyeOff,
  LogOut,
  CalendarIcon,
} from "lucide-react"

export default function SettingsPage() {
  const router = useRouter()
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)
  const [message, setMessage] = useState<{ type: "success" | "error"; text: string } | null>(null)

  // User data
  const [user, setUser] = useState<UserDTO | null>(null)
  const [userInfo, setUserInfo] = useState<UserInfoDTO | null>(null)
  const [userGoal, setUserGoal] = useState<UserGoalDTO | null>(null)
  const [preferences, setPreferences] = useState<UserPreferencesDTO | null>(null)

  // Limits state
  const [calorieLimit, setCalorieLimit] = useState(2000)
  const [proteinLimit, setProteinLimit] = useState(150)
  const [carbLimit, setCarbLimit] = useState(250)
  const [fatLimit, setFatLimit] = useState(65)
  const [waterGoal, setWaterGoal] = useState(2500)
  const [limitsExist, setLimitsExist] = useState(false)

  // Profile state
  const [name, setName] = useState("")
  const [surname, setSurname] = useState("")
  const [age, setAge] = useState("")
  const [height, setHeight] = useState("")
  const [lifestyle, setLifestyle] = useState("moderate")

  // Goals state
  const [goalType, setGoalType] = useState("maintain")
  const [goalWeight, setGoalWeight] = useState("")
  const [goalDate, setGoalDate] = useState<Date | undefined>(undefined)

  // Preferences state
  const [language, setLanguage] = useState("en")
  const [darkmode, setDarkmode] = useState(false)
  const [notifications, setNotifications] = useState(true)

  // Weight state
  const [currentWeight, setCurrentWeight] = useState<number | undefined>()
  const [newWeight, setNewWeight] = useState("")

  // Dialog states
  const [usernameDialogOpen, setUsernameDialogOpen] = useState(false)
  const [emailDialogOpen, setEmailDialogOpen] = useState(false)
  const [passwordDialogOpen, setPasswordDialogOpen] = useState(false)
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false)
  const [newUsername, setNewUsername] = useState("")
  const [newEmail, setNewEmail] = useState("")
  const [oldPassword, setOldPassword] = useState("")
  const [newPassword, setNewPassword] = useState("")
  const [confirmNewPassword, setConfirmNewPassword] = useState("")
  const [showOldPassword, setShowOldPassword] = useState(false)
  const [showNewPassword, setShowNewPassword] = useState(false)
  const [dialogLoading, setDialogLoading] = useState(false)
  const [dialogError, setDialogError] = useState("")

  useEffect(() => {
    async function loadSettings() {
      setLoading(true)
      const [limits, lastWeight, userData, userInfoData, goalData, prefsData] = await Promise.all([
        getDailyLimits(),
        getLastWeight(),
        getCurrentUser(),
        getUserInfo(),
        getUserGoal(),
        getUserPreferences(),
      ])

      if (limits) {
        setLimitsExist(true)
        setCalorieLimit(limits.calorieLimit)
        setProteinLimit(limits.proteinLimit)
        setCarbLimit(limits.carbLimit)
        setFatLimit(limits.fatLimit)
        setWaterGoal(limits.waterGoal)
      }

      if (lastWeight) {
        setCurrentWeight(lastWeight.weight)
      }

      if (userData) {
        setUser(userData)
      }

      if (userInfoData) {
        setUserInfo(userInfoData)
        setName(userInfoData.name || "")
        setSurname(userInfoData.surname || "")
        setAge(userInfoData.age?.toString() || "")
        setHeight(userInfoData.height?.toString() || "")
        setLifestyle(userInfoData.lifestyle || "moderate")
      }

      if (goalData) {
        setUserGoal(goalData)
        setGoalType(goalData.goalType || "maintain")
        setGoalWeight(goalData.goalWeight?.toString() || "")
        if (goalData.deadline) {
          setGoalDate(parseISO(goalData.deadline))
        }
      }

      if (prefsData) {
        setPreferences(prefsData)
        setLanguage(prefsData.language || "en")
        setDarkmode(prefsData.darkmode || false)
        setNotifications(prefsData.notifications ?? true)
      }

      setLoading(false)
    }
    loadSettings()
  }, [])

  const handleSaveLimits = async () => {
    setSaving(true)
    setMessage(null)

    const limits = {
      calorieLimit,
      proteinLimit,
      fatLimit,
      carbLimit,
      waterGoal,
    }

    const result = limitsExist ? await updateDailyLimits(limits) : await createDailyLimits(limits)

    if (result) {
      setLimitsExist(true)
      setMessage({ type: "success", text: "Nutrition goals saved successfully!" })
    } else {
      setMessage({ type: "error", text: "Failed to save nutrition goals." })
    }

    setSaving(false)
  }

  const handleSaveProfile = async () => {
    setSaving(true)
    setMessage(null)

    const bmi = currentWeight && height 
      ? Number((Number(currentWeight) / Math.pow(Number(height) / 100, 2)).toFixed(1))
      : userInfo?.bmi || 0

    const result = await updateUserInfo({
      name,
      surname,
      age: Number(age),
      weight: currentWeight || userInfo?.weight || 0,
      height: Number(height),
      lifestyle,
      bmi,
    })

    if (result) {
      setUserInfo(result)
      setMessage({ type: "success", text: "Profile updated successfully!" })
    } else {
      setMessage({ type: "error", text: "Failed to update profile." })
    }

    setSaving(false)
  }

  const handleSaveGoal = async () => {
    setSaving(true)
    setMessage(null)

    const result = await updateUserGoal({
      goalType,
      goalWeight: Number(goalWeight),
      goalDate: goalDate ? format(goalDate, "yyyy-MM-dd") : "",
    })

    if (result) {
      setUserGoal(result)
      setMessage({ type: "success", text: "Goal updated successfully!" })
    } else {
      setMessage({ type: "error", text: "Failed to update goal." })
    }

    setSaving(false)
  }

  const handleSavePreferences = async () => {
    setSaving(true)
    setMessage(null)

    const result = await updateUserPreferences({
      language,
      darkmode,
      font: preferences?.font || "default",
      notifications,
    })

    if (result) {
      setPreferences(result)
      setMessage({ type: "success", text: "Preferences saved successfully!" })
    } else {
      setMessage({ type: "error", text: "Failed to save preferences." })
    }

    setSaving(false)
  }

  const handleLogWeight = async () => {
    if (!newWeight) return

    setSaving(true)
    const today = new Date().toISOString().split("T")[0]
    const result = await addWeight(Number(newWeight), today)

    if (result) {
      setCurrentWeight(result.weight)
      setNewWeight("")
      setMessage({ type: "success", text: "Weight logged successfully!" })
    } else {
      setMessage({ type: "error", text: "Failed to log weight." })
    }

    setSaving(false)
  }

  const handleChangeUsername = async () => {
    if (!newUsername.trim()) {
      setDialogError("Please enter a username")
      return
    }
    setDialogLoading(true)
    setDialogError("")

    const result = await changeUsername(newUsername)

    if (result.success) {
      setUser((prev) => (prev ? { ...prev, username: newUsername } : null))
      setUsernameDialogOpen(false)
      setNewUsername("")
      setMessage({ type: "success", text: "Username changed successfully!" })
    } else {
      setDialogError(result.message || "Failed to change username")
    }

    setDialogLoading(false)
  }

  const handleChangeEmail = async () => {
    if (!newEmail.trim()) {
      setDialogError("Please enter an email")
      return
    }
    setDialogLoading(true)
    setDialogError("")

    const result = await changeEmail(newEmail)

    if (result.success) {
      setUser((prev) => (prev ? { ...prev, email: newEmail } : null))
      setEmailDialogOpen(false)
      setNewEmail("")
      setMessage({ type: "success", text: "Email changed successfully!" })
    } else {
      setDialogError(result.message || "Failed to change email")
    }

    setDialogLoading(false)
  }

  const handleChangePassword = async () => {
    if (!oldPassword || !newPassword) {
      setDialogError("Please fill in all fields")
      return
    }
    if (newPassword !== confirmNewPassword) {
      setDialogError("New passwords do not match")
      return
    }
    if (newPassword.length < 8) {
      setDialogError("New password must be at least 8 characters")
      return
    }
    setDialogLoading(true)
    setDialogError("")

    const result = await changePassword(oldPassword, newPassword)

    if (result.success) {
      setPasswordDialogOpen(false)
      setOldPassword("")
      setNewPassword("")
      setConfirmNewPassword("")
      setMessage({ type: "success", text: "Password changed successfully!" })
    } else {
      setDialogError(result.message || "Failed to change password")
    }

    setDialogLoading(false)
  }

  const handleLogout = () => {
    removeToken()
    router.push("/login")
  }

  const handleDeleteAccount = () => {
    // Note: You'll need a delete account endpoint in your backend
    setDialogError("Account deletion is not yet implemented. Please contact support.")
  }

  if (loading) {
    return (
      <div className="flex min-h-[400px] items-center justify-center">
        <div className="h-8 w-8 animate-spin rounded-full border-4 border-primary border-t-transparent" />
      </div>
    )
  }

  return (
    <div className="mx-auto max-w-3xl space-y-6 pb-8">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-foreground sm:text-3xl">Settings</h1>
          <p className="mt-1 text-muted-foreground">Manage your account and preferences</p>
        </div>
        <Button variant="outline" onClick={handleLogout} className="gap-2 bg-transparent">
          <LogOut className="h-4 w-4" />
          Sign Out
        </Button>
      </div>

      {/* Message */}
      {message && (
        <div
          className={`flex items-center gap-2 rounded-lg p-4 ${
            message.type === "success"
              ? "bg-secondary/20 text-secondary"
              : "bg-destructive/20 text-destructive"
          }`}
        >
          {message.type === "success" ? (
            <Check className="h-4 w-4" />
          ) : (
            <AlertTriangle className="h-4 w-4" />
          )}
          {message.text}
        </div>
      )}

      {/* Account Info */}
      <Card>
        <CardHeader>
          <div className="flex items-center gap-3">
            <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-primary/20">
              <User className="h-5 w-5 text-primary" />
            </div>
            <div>
              <CardTitle>Account</CardTitle>
              <CardDescription>Manage your account credentials</CardDescription>
            </div>
          </div>
        </CardHeader>
        <CardContent className="space-y-4">
          {/* Current Info Display */}
          <div className="grid gap-4 sm:grid-cols-2">
            <div className="rounded-lg bg-muted/50 p-4">
              <p className="text-sm text-muted-foreground">Username</p>
              <p className="font-medium text-foreground">{user?.username || "Not set"}</p>
            </div>
            <div className="rounded-lg bg-muted/50 p-4">
              <p className="text-sm text-muted-foreground">Email</p>
              <p className="font-medium text-foreground">{user?.email || "Not set"}</p>
            </div>
          </div>

          <Separator />

          {/* Action Buttons */}
          <div className="flex flex-wrap gap-3">
            {/* Change Username Dialog */}
            <Dialog open={usernameDialogOpen} onOpenChange={setUsernameDialogOpen}>
              <DialogTrigger asChild>
                <Button variant="outline" className="gap-2 bg-transparent">
                  <User className="h-4 w-4" />
                  Change Username
                </Button>
              </DialogTrigger>
              <DialogContent>
                <DialogHeader>
                  <DialogTitle>Change Username</DialogTitle>
                  <DialogDescription>Enter your new username below.</DialogDescription>
                </DialogHeader>
                {dialogError && (
                  <div className="rounded-lg bg-destructive/10 p-3 text-sm text-destructive">
                    {dialogError}
                  </div>
                )}
                <div className="space-y-2">
                  <Label htmlFor="newUsername">New Username</Label>
                  <Input
                    id="newUsername"
                    value={newUsername}
                    onChange={(e) => setNewUsername(e.target.value)}
                    placeholder="Enter new username"
                  />
                </div>
                <DialogFooter>
                  <Button
                    variant="outline"
                    onClick={() => setUsernameDialogOpen(false)}
                    className="bg-transparent"
                  >
                    Cancel
                  </Button>
                  <Button onClick={handleChangeUsername} disabled={dialogLoading}>
                    {dialogLoading ? (
                      <Loader2 className="h-4 w-4 animate-spin" />
                    ) : (
                      "Save"
                    )}
                  </Button>
                </DialogFooter>
              </DialogContent>
            </Dialog>

            {/* Change Email Dialog */}
            <Dialog open={emailDialogOpen} onOpenChange={setEmailDialogOpen}>
              <DialogTrigger asChild>
                <Button variant="outline" className="gap-2 bg-transparent">
                  <Mail className="h-4 w-4" />
                  Change Email
                </Button>
              </DialogTrigger>
              <DialogContent>
                <DialogHeader>
                  <DialogTitle>Change Email</DialogTitle>
                  <DialogDescription>Enter your new email address below.</DialogDescription>
                </DialogHeader>
                {dialogError && (
                  <div className="rounded-lg bg-destructive/10 p-3 text-sm text-destructive">
                    {dialogError}
                  </div>
                )}
                <div className="space-y-2">
                  <Label htmlFor="newEmail">New Email</Label>
                  <Input
                    id="newEmail"
                    type="email"
                    value={newEmail}
                    onChange={(e) => setNewEmail(e.target.value)}
                    placeholder="Enter new email"
                  />
                </div>
                <DialogFooter>
                  <Button
                    variant="outline"
                    onClick={() => setEmailDialogOpen(false)}
                    className="bg-transparent"
                  >
                    Cancel
                  </Button>
                  <Button onClick={handleChangeEmail} disabled={dialogLoading}>
                    {dialogLoading ? (
                      <Loader2 className="h-4 w-4 animate-spin" />
                    ) : (
                      "Save"
                    )}
                  </Button>
                </DialogFooter>
              </DialogContent>
            </Dialog>

            {/* Change Password Dialog */}
            <Dialog open={passwordDialogOpen} onOpenChange={setPasswordDialogOpen}>
              <DialogTrigger asChild>
                <Button variant="outline" className="gap-2 bg-transparent">
                  <Lock className="h-4 w-4" />
                  Change Password
                </Button>
              </DialogTrigger>
              <DialogContent>
                <DialogHeader>
                  <DialogTitle>Change Password</DialogTitle>
                  <DialogDescription>Enter your current and new password.</DialogDescription>
                </DialogHeader>
                {dialogError && (
                  <div className="rounded-lg bg-destructive/10 p-3 text-sm text-destructive">
                    {dialogError}
                  </div>
                )}
                <div className="space-y-4">
                  <div className="space-y-2">
                    <Label htmlFor="oldPassword">Current Password</Label>
                    <div className="relative">
                      <Input
                        id="oldPassword"
                        type={showOldPassword ? "text" : "password"}
                        value={oldPassword}
                        onChange={(e) => setOldPassword(e.target.value)}
                        placeholder="Enter current password"
                      />
                      <button
                        type="button"
                        onClick={() => setShowOldPassword(!showOldPassword)}
                        className="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground"
                      >
                        {showOldPassword ? <EyeOff className="h-4 w-4" /> : <Eye className="h-4 w-4" />}
                      </button>
                    </div>
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="newPassword">New Password</Label>
                    <div className="relative">
                      <Input
                        id="newPassword"
                        type={showNewPassword ? "text" : "password"}
                        value={newPassword}
                        onChange={(e) => setNewPassword(e.target.value)}
                        placeholder="Enter new password"
                      />
                      <button
                        type="button"
                        onClick={() => setShowNewPassword(!showNewPassword)}
                        className="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground"
                      >
                        {showNewPassword ? <EyeOff className="h-4 w-4" /> : <Eye className="h-4 w-4" />}
                      </button>
                    </div>
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="confirmNewPassword">Confirm New Password</Label>
                    <Input
                      id="confirmNewPassword"
                      type="password"
                      value={confirmNewPassword}
                      onChange={(e) => setConfirmNewPassword(e.target.value)}
                      placeholder="Confirm new password"
                    />
                  </div>
                </div>
                <DialogFooter>
                  <Button
                    variant="outline"
                    onClick={() => setPasswordDialogOpen(false)}
                    className="bg-transparent"
                  >
                    Cancel
                  </Button>
                  <Button onClick={handleChangePassword} disabled={dialogLoading}>
                    {dialogLoading ? (
                      <Loader2 className="h-4 w-4 animate-spin" />
                    ) : (
                      "Change Password"
                    )}
                  </Button>
                </DialogFooter>
              </DialogContent>
            </Dialog>
          </div>
        </CardContent>
      </Card>

      {/* Profile Info */}
      <Card>
        <CardHeader>
          <div className="flex items-center gap-3">
            <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-secondary/20">
              <User className="h-5 w-5 text-secondary" />
            </div>
            <div>
              <CardTitle>Personal Information</CardTitle>
              <CardDescription>Your profile details</CardDescription>
            </div>
          </div>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="grid gap-4 sm:grid-cols-2">
            <div className="space-y-2">
              <Label htmlFor="name">First Name</Label>
              <Input
                id="name"
                value={name}
                onChange={(e) => setName(e.target.value)}
                placeholder="John"
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="surname">Last Name</Label>
              <Input
                id="surname"
                value={surname}
                onChange={(e) => setSurname(e.target.value)}
                placeholder="Doe"
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="age">Age</Label>
              <Input
                id="age"
                type="number"
                value={age}
                onChange={(e) => setAge(e.target.value)}
                placeholder="25"
                min={13}
                max={120}
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="height">Height (cm)</Label>
              <Input
                id="height"
                type="number"
                value={height}
                onChange={(e) => setHeight(e.target.value)}
                placeholder="175"
                min={100}
                max={250}
              />
            </div>
          </div>

          <div className="space-y-2">
            <Label htmlFor="lifestyle">Activity Level</Label>
            <Select value={lifestyle} onValueChange={setLifestyle}>
              <SelectTrigger>
                <SelectValue placeholder="Select activity level" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="sedentary">Sedentary (little to no exercise)</SelectItem>
                <SelectItem value="light">Lightly Active (1-3 days/week)</SelectItem>
                <SelectItem value="moderate">Moderately Active (3-5 days/week)</SelectItem>
                <SelectItem value="active">Very Active (6-7 days/week)</SelectItem>
                <SelectItem value="athlete">Athlete (intense daily training)</SelectItem>
              </SelectContent>
            </Select>
          </div>

          <Button onClick={handleSaveProfile} disabled={saving} className="gap-2">
            <Save className="h-4 w-4" />
            {saving ? "Saving..." : "Save Profile"}
          </Button>
        </CardContent>
      </Card>

      {/* Weight Tracking */}
      <Card>
        <CardHeader>
          <div className="flex items-center gap-3">
            <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-accent/20">
              <Scale className="h-5 w-5 text-accent" />
            </div>
            <div>
              <CardTitle>Weight Tracking</CardTitle>
              <CardDescription>Log your current weight to track progress</CardDescription>
            </div>
          </div>
        </CardHeader>
        <CardContent className="space-y-4">
          {currentWeight && (
            <div className="rounded-lg bg-muted/50 p-4">
              <p className="text-sm text-muted-foreground">Current Weight</p>
              <p className="text-2xl font-bold text-foreground">{currentWeight} kg</p>
            </div>
          )}

          <div className="flex gap-3">
            <div className="flex-1 space-y-2">
              <Label htmlFor="newWeight">Log New Weight</Label>
              <div className="flex items-center gap-2">
                <Input
                  id="newWeight"
                  type="number"
                  value={newWeight}
                  onChange={(e) => setNewWeight(e.target.value)}
                  placeholder="Enter weight"
                  step="0.1"
                  min={20}
                  max={500}
                />
                <span className="text-sm text-muted-foreground">kg</span>
              </div>
            </div>
          </div>

          <Button
            onClick={handleLogWeight}
            disabled={saving || !newWeight}
            variant="outline"
            className="gap-2 bg-transparent"
          >
            <Scale className="h-4 w-4" />
            Log Weight
          </Button>
        </CardContent>
      </Card>

      {/* Fitness Goal */}
      <Card>
        <CardHeader>
          <div className="flex items-center gap-3">
            <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-primary/20">
              <Target className="h-5 w-5 text-primary" />
            </div>
            <div>
              <CardTitle>Fitness Goal</CardTitle>
              <CardDescription>Set your target weight and deadline</CardDescription>
            </div>
          </div>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="space-y-2">
            <Label htmlFor="goalType">Goal Type</Label>
            <Select value={goalType} onValueChange={setGoalType}>
              <SelectTrigger>
                <SelectValue placeholder="Select goal type" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="lose">Lose Weight</SelectItem>
                <SelectItem value="maintain">Maintain Weight</SelectItem>
                <SelectItem value="gain">Gain Weight</SelectItem>
              </SelectContent>
            </Select>
          </div>

          <div className="grid gap-4 sm:grid-cols-2">
            <div className="space-y-2">
              <Label htmlFor="goalWeight">Target Weight (kg)</Label>
              <Input
                id="goalWeight"
                type="number"
                value={goalWeight}
                onChange={(e) => setGoalWeight(e.target.value)}
                placeholder="65"
                step="0.1"
                min={20}
                max={500}
              />
            </div>
            <div className="space-y-2 flex flex-col">
              <Label htmlFor="goalDate" className="mb-2">Target Date</Label>
              <Popover>
                <PopoverTrigger asChild>
                  <Button
                    variant="outline"
                    className={cn(
                      "w-full justify-start text-left font-normal",
                      !goalDate && "text-muted-foreground"
                    )}
                  >
                    <CalendarIcon className="mr-2 h-4 w-4" />
                    {goalDate ? format(goalDate, "EEE, MMM d, yyyy") : <span>Pick a date</span>}
                  </Button>
                </PopoverTrigger>
                <PopoverContent className="w-auto p-0" align="end">
                  <Calendar
                    mode="single"
                    selected={goalDate}
                    onSelect={setGoalDate}
                    disabled={(date) => date < new Date()}
                    initialFocus
                  />
                </PopoverContent>
              </Popover>
            </div>
          </div>

          <Button onClick={handleSaveGoal} disabled={saving} className="gap-2">
            <Save className="h-4 w-4" />
            {saving ? "Saving..." : "Save Goal"}
          </Button>
        </CardContent>
      </Card>

      {/* Nutrition Goals */}
      <Card>
        <CardHeader>
          <div className="flex items-center gap-3">
            <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-secondary/20">
              <Target className="h-5 w-5 text-secondary" />
            </div>
            <div>
              <CardTitle>Daily Nutrition Goals</CardTitle>
              <CardDescription>Set your daily calorie and macro targets</CardDescription>
            </div>
          </div>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="grid gap-4 sm:grid-cols-2">
            <div className="space-y-2">
              <Label htmlFor="calorieLimit">Daily Calorie Limit</Label>
              <div className="flex items-center gap-2">
                <Input
                  id="calorieLimit"
                  type="number"
                  value={calorieLimit}
                  onChange={(e) => setCalorieLimit(Number(e.target.value))}
                  min={1000}
                  max={10000}
                />
                <span className="text-sm text-muted-foreground">kcal</span>
              </div>
            </div>

            <div className="space-y-2">
              <Label htmlFor="proteinLimit">Protein Goal</Label>
              <div className="flex items-center gap-2">
                <Input
                  id="proteinLimit"
                  type="number"
                  value={proteinLimit}
                  onChange={(e) => setProteinLimit(Number(e.target.value))}
                  min={0}
                />
                <span className="text-sm text-muted-foreground">g</span>
              </div>
            </div>

            <div className="space-y-2">
              <Label htmlFor="carbLimit">Carbohydrates Goal</Label>
              <div className="flex items-center gap-2">
                <Input
                  id="carbLimit"
                  type="number"
                  value={carbLimit}
                  onChange={(e) => setCarbLimit(Number(e.target.value))}
                  min={0}
                />
                <span className="text-sm text-muted-foreground">g</span>
              </div>
            </div>

            <div className="space-y-2">
              <Label htmlFor="fatLimit">Fat Goal</Label>
              <div className="flex items-center gap-2">
                <Input
                  id="fatLimit"
                  type="number"
                  value={fatLimit}
                  onChange={(e) => setFatLimit(Number(e.target.value))}
                  min={0}
                />
                <span className="text-sm text-muted-foreground">g</span>
              </div>
            </div>
          </div>

          <Separator />

          <div className="space-y-2">
            <Label htmlFor="waterGoal">Daily Water Goal</Label>
            <div className="flex items-center gap-2">
              <Input
                id="waterGoal"
                type="number"
                value={waterGoal}
                onChange={(e) => setWaterGoal(Number(e.target.value))}
                min={500}
                max={10000}
                className="max-w-[200px]"
              />
              <span className="text-sm text-muted-foreground">ml</span>
            </div>
            <p className="text-xs text-muted-foreground">Recommended: 2000-3000ml per day</p>
          </div>

          <Button onClick={handleSaveLimits} disabled={saving} className="gap-2">
            <Save className="h-4 w-4" />
            {saving ? "Saving..." : "Save Nutrition Goals"}
          </Button>
        </CardContent>
      </Card>

      {/* Preferences */}
      <Card>
        <CardHeader>
          <div className="flex items-center gap-3">
            <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-muted">
              <Palette className="h-5 w-5 text-muted-foreground" />
            </div>
            <div>
              <CardTitle>Preferences</CardTitle>
              <CardDescription>Customize your app experience</CardDescription>
            </div>
          </div>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-3">
              <Globe className="h-5 w-5 text-muted-foreground" />
              <div>
                <p className="font-medium text-foreground">Language</p>
                <p className="text-sm text-muted-foreground">Choose your preferred language</p>
              </div>
            </div>
            <Select value={language} onValueChange={setLanguage}>
              <SelectTrigger className="w-[140px]">
                <SelectValue placeholder="Language" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="en">English</SelectItem>
                <SelectItem value="es">Spanish</SelectItem>
                <SelectItem value="fr">French</SelectItem>
                <SelectItem value="de">German</SelectItem>
              </SelectContent>
            </Select>
          </div>

          <Separator />

          <div className="flex items-center justify-between">
            <div className="flex items-center gap-3">
              <Palette className="h-5 w-5 text-muted-foreground" />
              <div>
                <p className="font-medium text-foreground">Dark Mode</p>
                <p className="text-sm text-muted-foreground">Use dark theme</p>
              </div>
            </div>
            <Switch checked={darkmode} onCheckedChange={setDarkmode} />
          </div>

          <Separator />

          <div className="flex items-center justify-between">
            <div className="flex items-center gap-3">
              <Bell className="h-5 w-5 text-muted-foreground" />
              <div>
                <p className="font-medium text-foreground">Notifications</p>
                <p className="text-sm text-muted-foreground">Receive reminders and updates</p>
              </div>
            </div>
            <Switch checked={notifications} onCheckedChange={setNotifications} />
          </div>

          <Button onClick={handleSavePreferences} disabled={saving} variant="outline" className="gap-2 bg-transparent">
            <Save className="h-4 w-4" />
            {saving ? "Saving..." : "Save Preferences"}
          </Button>
        </CardContent>
      </Card>

      {/* Danger Zone */}
      <Card className="border-destructive/50">
        <CardHeader>
          <div className="flex items-center gap-3">
            <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-destructive/20">
              <AlertTriangle className="h-5 w-5 text-destructive" />
            </div>
            <div>
              <CardTitle className="text-destructive">Danger Zone</CardTitle>
              <CardDescription>Irreversible actions</CardDescription>
            </div>
          </div>
        </CardHeader>
        <CardContent>
          <Dialog open={deleteDialogOpen} onOpenChange={setDeleteDialogOpen}>
            <DialogTrigger asChild>
              <Button variant="destructive" className="gap-2">
                <Trash2 className="h-4 w-4" />
                Delete Account
              </Button>
            </DialogTrigger>
            <DialogContent>
              <DialogHeader>
                <DialogTitle>Delete Account</DialogTitle>
                <DialogDescription>
                  Are you sure you want to delete your account? This action cannot be undone.
                  All your data, including meals, exercises, and progress will be permanently deleted.
                </DialogDescription>
              </DialogHeader>
              {dialogError && (
                <div className="rounded-lg bg-destructive/10 p-3 text-sm text-destructive">
                  {dialogError}
                </div>
              )}
              <DialogFooter>
                <Button
                  variant="outline"
                  onClick={() => setDeleteDialogOpen(false)}
                  className="bg-transparent"
                >
                  Cancel
                </Button>
                <Button variant="destructive" onClick={handleDeleteAccount} disabled={dialogLoading}>
                  {dialogLoading ? (
                    <Loader2 className="h-4 w-4 animate-spin" />
                  ) : (
                    "Delete Account"
                  )}
                </Button>
              </DialogFooter>
            </DialogContent>
          </Dialog>
        </CardContent>
      </Card>
    </div>
  )
}
