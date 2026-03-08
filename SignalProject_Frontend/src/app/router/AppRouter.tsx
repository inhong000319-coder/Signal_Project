import { Navigate, Route, Routes } from "react-router-dom";
import { LandingPage } from "@/pages/landing/LandingPage";
import { LoginPage } from "@/pages/auth/LoginPage";
import { RegisterPage } from "@/pages/auth/RegisterPage";
import { FriendPage } from "@/pages/friend/FriendPage";
import { ConversationListPage } from "@/pages/conversation/ConversationListPage";
import { ChatRoomPage } from "@/pages/conversation/ChatRoomPage";
import { ErrorPage } from "@/pages/error/ErrorPage";

export function AppRouter() {
  return (
    <Routes>
      <Route path="/" element={<LandingPage />} />
      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />
      <Route path="/friends" element={<FriendPage />} />
      <Route path="/conversations" element={<ConversationListPage />} />
      <Route path="/conversations/:conversationId" element={<ChatRoomPage />} />
      <Route path="/error" element={<ErrorPage />} />
      <Route path="*" element={<Navigate to="/error" replace />} />
    </Routes>
  );
}
