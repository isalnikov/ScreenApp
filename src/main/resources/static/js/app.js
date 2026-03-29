// ScreenApp - ARM Контента Frontend Application

// API Configuration
const API_BASE_URL = '/api/v1';

// State management
const state = {
    currentSection: 'screens',
    screens: [],
    content: [],
    playlists: [],
    schedules: [],
    stats: {}
};

// Initialize application
document.addEventListener('DOMContentLoaded', () => {
    initializeNavigation();
    initializeModals();
    loadInitialData();
});

// Navigation
function initializeNavigation() {
    const navButtons = document.querySelectorAll('.nav-btn');
    
    navButtons.forEach(btn => {
        btn.addEventListener('click', () => {
            // Remove active class from all buttons and sections
            navButtons.forEach(b => b.classList.remove('active'));
            document.querySelectorAll('.section').forEach(s => s.classList.remove('active'));
            
            // Add active class to clicked button
            btn.classList.add('active');
            
            // Show corresponding section
            const sectionId = btn.id.replace('btn-', '') + '-section';
            document.getElementById(sectionId).classList.add('active');
            
            // Load data for the section
            loadSectionData(btn.id.replace('btn-', ''));
        });
    });
}

// Load initial data
async function loadInitialData() {
    showLoading(true);
    try {
        await Promise.all([
            loadScreens(),
            loadContent(),
            loadPlaylists(),
            loadSchedules(),
            loadStats()
        ]);
    } catch (error) {
        showNotification('Ошибка загрузки данных: ' + error.message, 'error');
    } finally {
        showLoading(false);
    }
}

// Load section data
function loadSectionData(section) {
    switch(section) {
        case 'screens':
            renderScreens();
            break;
        case 'content':
            renderContent();
            break;
        case 'playlists':
            renderPlaylists();
            break;
        case 'schedules':
            renderSchedules();
            break;
        case 'stats':
            renderStats();
            break;
    }
}

// API Functions
async function apiCall(endpoint, method = 'GET', data = null) {
    const options = {
        method,
        headers: {
            'Content-Type': 'application/json',
        }
    };
    
    if (data) {
        options.body = JSON.stringify(data);
    }
    
    const response = await fetch(`${API_BASE_URL}${endpoint}`, options);
    
    if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
    }
    
    return await response.json();
}

// Screens
async function loadScreens() {
    try {
        state.screens = await apiCall('/screens');
        renderScreens();
    } catch (error) {
        console.error('Error loading screens:', error);
        // Demo data for development
        state.screens = generateDemoScreens();
        renderScreens();
    }
}

function renderScreens() {
    const tbody = document.getElementById('screens-tbody');
    tbody.innerHTML = '';
    
    state.screens.forEach(screen => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${screen.id}</td>
            <td>${screen.name}</td>
            <td>${screen.location}</td>
            <td>${screen.region || 'Не указан'}</td>
            <td><span class="status-badge status-${screen.status}">${translateStatus(screen.status)}</span></td>
            <td>${formatDate(screen.lastConnection)}</td>
            <td>
                <button class="btn-edit" onclick="editScreen(${screen.id})">✏️</button>
                <button class="btn-delete" onclick="deleteScreen(${screen.id})">🗑️</button>
            </td>
        `;
        tbody.appendChild(tr);
    });
    
    updateStatsFromScreens();
}

// Content
async function loadContent() {
    try {
        state.content = await apiCall('/content');
        renderContent();
    } catch (error) {
        console.error('Error loading content:', error);
        // Demo data for development
        state.content = generateDemoContent();
        renderContent();
    }
}

function renderContent() {
    const grid = document.getElementById('content-grid');
    grid.innerHTML = '';
    
    state.content.forEach(item => {
        const card = document.createElement('div');
        card.className = 'content-card';
        card.innerHTML = `
            <div class="content-preview">
                ${getContentPreview(item)}
            </div>
            <div class="content-info">
                <h4>${item.name}</h4>
                <p class="content-meta">${translateContentType(item.type)} • ${formatFileSize(item.size)}</p>
                <div class="content-actions">
                    <button class="btn-edit" onclick="editContent(${item.id})">✏️</button>
                    <button class="btn-delete" onclick="deleteContent(${item.id})">🗑️</button>
                </div>
            </div>
        `;
        grid.appendChild(card);
    });
}

// Playlists
async function loadPlaylists() {
    try {
        state.playlists = await apiCall('/playlists');
        renderPlaylists();
    } catch (error) {
        console.error('Error loading playlists:', error);
        // Demo data for development
        state.playlists = generateDemoPlaylists();
        renderPlaylists();
    }
}

function renderPlaylists() {
    const tbody = document.getElementById('playlists-tbody');
    tbody.innerHTML = '';
    
    state.playlists.forEach(playlist => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${playlist.id}</td>
            <td>${playlist.name}</td>
            <td>${playlist.itemCount}</td>
            <td>${formatDuration(playlist.totalDuration)}</td>
            <td>${formatDate(playlist.createdAt)}</td>
            <td>
                <button class="btn-edit" onclick="editPlaylist(${playlist.id})">✏️</button>
                <button class="btn-delete" onclick="deletePlaylist(${playlist.id})">🗑️</button>
            </td>
        `;
        tbody.appendChild(tr);
    });
}

// Schedules
async function loadSchedules() {
    try {
        state.schedules = await apiCall('/schedules');
        renderSchedules();
    } catch (error) {
        console.error('Error loading schedules:', error);
        // Demo data for development
        state.schedules = generateDemoSchedules();
        renderSchedules();
    }
}

function renderSchedules() {
    const tbody = document.getElementById('schedules-tbody');
    tbody.innerHTML = '';
    
    state.schedules.forEach(schedule => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${schedule.id}</td>
            <td>${schedule.name}</td>
            <td>${schedule.playlistName}</td>
            <td>${schedule.screenCount} экранов</td>
            <td>${formatDate(schedule.startDate)} - ${formatDate(schedule.endDate)}</td>
            <td><span class="status-badge status-${schedule.active ? 'online' : 'offline'}">${schedule.active ? 'Активно' : 'Неактивно'}</span></td>
            <td>
                <button class="btn-edit" onclick="editSchedule(${schedule.id})">✏️</button>
                <button class="btn-delete" onclick="deleteSchedule(${schedule.id})">🗑️</button>
            </td>
        `;
        tbody.appendChild(tr);
    });
}

// Stats
async function loadStats() {
    try {
        state.stats = await apiCall('/stats');
        renderStats();
    } catch (error) {
        console.error('Error loading stats:', error);
        updateStatsFromScreens();
    }
}

function renderStats() {
    document.getElementById('stat-total-screens').textContent = state.screens.length;
    document.getElementById('stat-online-screens').textContent = state.screens.filter(s => s.status === 'online').length;
    document.getElementById('stat-offline-screens').textContent = state.screens.filter(s => s.status === 'offline').length;
    document.getElementById('stat-error-screens').textContent = state.screens.filter(s => s.status === 'error').length;
}

function updateStatsFromScreens() {
    if (document.getElementById('stats-section').classList.contains('active')) {
        renderStats();
    }
}

// Modal Functions
function initializeModals() {
    const modalClose = document.getElementById('modal-close');
    const modalOverlay = document.getElementById('modal-overlay');
    
    modalClose.addEventListener('click', () => {
        closeModal();
    });
    
    modalOverlay.addEventListener('click', (e) => {
        if (e.target === modalOverlay) {
            closeModal();
        }
    });
    
    // Button event listeners
    document.getElementById('btn-add-screen').addEventListener('click', () => showAddScreenModal());
    document.getElementById('btn-add-content').addEventListener('click', () => showAddContentModal());
    document.getElementById('btn-add-playlist').addEventListener('click', () => showAddPlaylistModal());
    document.getElementById('btn-add-schedule').addEventListener('click', () => showAddScheduleModal());
}

function showModal(title, content) {
    document.getElementById('modal-title').textContent = title;
    document.getElementById('modal-body').innerHTML = content;
    document.getElementById('modal-overlay').classList.add('active');
}

function closeModal() {
    document.getElementById('modal-overlay').classList.remove('active');
}

// Modal Content Generators
function showAddScreenModal() {
    const content = `
        <form id="add-screen-form">
            <div class="form-group">
                <label for="screen-name">Название экрана</label>
                <input type="text" id="screen-name" required>
            </div>
            <div class="form-group">
                <label for="screen-location">Локация</label>
                <input type="text" id="screen-location" required>
            </div>
            <div class="form-group">
                <label for="screen-region">Регион</label>
                <select id="screen-region">
                    <option value="Москва">Москва</option>
                    <option value="Санкт-Петербург">Санкт-Петербург</option>
                    <option value="Казань">Казань</option>
                    <option value="Екатеринбург">Екатеринбург</option>
                    <option value="Новосибирск">Новосибирск</option>
                </select>
            </div>
            <div class="form-actions">
                <button type="button" class="btn-small" onclick="closeModal()">Отмена</button>
                <button type="submit" class="btn-primary">Сохранить</button>
            </div>
        </form>
    `;
    
    showModal('Добавить экран', content);
    
    document.getElementById('add-screen-form').addEventListener('submit', (e) => {
        e.preventDefault();
        saveScreen({
            name: document.getElementById('screen-name').value,
            location: document.getElementById('screen-location').value,
            region: document.getElementById('screen-region').value
        });
    });
}

function showAddContentModal() {
    const content = `
        <form id="add-content-form">
            <div class="form-group">
                <label for="content-name">Название</label>
                <input type="text" id="content-name" required>
            </div>
            <div class="form-group">
                <label for="content-type">Тип контента</label>
                <select id="content-type" required>
                    <option value="IMAGE">Изображение</option>
                    <option value="VIDEO">Видео</option>
                    <option value="TEXT">Текст</option>
                    <option value="HTML">HTML</option>
                </select>
            </div>
            <div class="form-group">
                <label for="content-file">Файл</label>
                <input type="file" id="content-file" required>
            </div>
            <div class="form-actions">
                <button type="button" class="btn-small" onclick="closeModal()">Отмена</button>
                <button type="submit" class="btn-primary">Загрузить</button>
            </div>
        </form>
    `;
    
    showModal('Добавить контент', content);
    
    document.getElementById('add-content-form').addEventListener('submit', (e) => {
        e.preventDefault();
        saveContent({
            name: document.getElementById('content-name').value,
            type: document.getElementById('content-type').value
        });
    });
}

function showAddPlaylistModal() {
    const content = `
        <form id="add-playlist-form">
            <div class="form-group">
                <label for="playlist-name">Название плейлиста</label>
                <input type="text" id="playlist-name" required>
            </div>
            <div class="form-group">
                <label for="playlist-content">Выберите контент</label>
                <select id="playlist-content" multiple required>
                    ${state.content.map(item => `<option value="${item.id}">${item.name}</option>`).join('')}
                </select>
            </div>
            <div class="form-actions">
                <button type="button" class="btn-small" onclick="closeModal()">Отмена</button>
                <button type="submit" class="btn-primary">Создать</button>
            </div>
        </form>
    `;
    
    showModal('Создать плейлист', content);
    
    document.getElementById('add-playlist-form').addEventListener('submit', (e) => {
        e.preventDefault();
        savePlaylist({
            name: document.getElementById('playlist-name').value,
            contentIds: Array.from(document.getElementById('playlist-content').selectedOptions).map(opt => opt.value)
        });
    });
}

function showAddScheduleModal() {
    const content = `
        <form id="add-schedule-form">
            <div class="form-group">
                <label for="schedule-name">Название расписания</label>
                <input type="text" id="schedule-name" required>
            </div>
            <div class="form-group">
                <label for="schedule-playlist">Плейлист</label>
                <select id="schedule-playlist" required>
                    ${state.playlists.map(pl => `<option value="${pl.id}">${pl.name}</option>`).join('')}
                </select>
            </div>
            <div class="form-group">
                <label for="schedule-start">Дата начала</label>
                <input type="date" id="schedule-start" required>
            </div>
            <div class="form-group">
                <label for="schedule-end">Дата окончания</label>
                <input type="date" id="schedule-end" required>
            </div>
            <div class="form-actions">
                <button type="button" class="btn-small" onclick="closeModal()">Отмена</button>
                <button type="submit" class="btn-primary">Создать</button>
            </div>
        </form>
    `;
    
    showModal('Создать расписание', content);
    
    document.getElementById('add-schedule-form').addEventListener('submit', (e) => {
        e.preventDefault();
        saveSchedule({
            name: document.getElementById('schedule-name').value,
            playlistId: document.getElementById('schedule-playlist').value,
            startDate: document.getElementById('schedule-start').value,
            endDate: document.getElementById('schedule-end').value
        });
    });
}

// Save functions
async function saveScreen(data) {
    try {
        await apiCall('/screens', 'POST', data);
        showNotification('Экран успешно добавлен', 'success');
        closeModal();
        loadScreens();
    } catch (error) {
        state.screens.push({
            id: state.screens.length + 1,
            ...data,
            status: 'offline',
            lastConnection: new Date().toISOString()
        });
        showNotification('Экран добавлен (демо режим)', 'success');
        closeModal();
        renderScreens();
    }
}

async function saveContent(data) {
    try {
        await apiCall('/content', 'POST', data);
        showNotification('Контент успешно загружен', 'success');
        closeModal();
        loadContent();
    } catch (error) {
        state.content.push({
            id: state.content.length + 1,
            ...data,
            size: Math.random() * 10000000,
            createdAt: new Date().toISOString()
        });
        showNotification('Контент добавлен (демо режим)', 'success');
        closeModal();
        renderContent();
    }
}

async function savePlaylist(data) {
    try {
        await apiCall('/playlists', 'POST', data);
        showNotification('Плейлист успешно создан', 'success');
        closeModal();
        loadPlaylists();
    } catch (error) {
        state.playlists.push({
            id: state.playlists.length + 1,
            ...data,
            itemCount: data.contentIds.length,
            totalDuration: data.contentIds.length * 30,
            createdAt: new Date().toISOString()
        });
        showNotification('Плейлист создан (демо режим)', 'success');
        closeModal();
        renderPlaylists();
    }
}

async function saveSchedule(data) {
    try {
        await apiCall('/schedules', 'POST', data);
        showNotification('Расписание успешно создано', 'success');
        closeModal();
        loadSchedules();
    } catch (error) {
        const playlist = state.playlists.find(p => p.id == data.playlistId);
        state.schedules.push({
            id: state.schedules.length + 1,
            ...data,
            playlistName: playlist ? playlist.name : 'Unknown',
            screenCount: Math.floor(Math.random() * 100) + 1,
            active: true
        });
        showNotification('Расписание создано (демо режим)', 'success');
        closeModal();
        renderSchedules();
    }
}

// Delete functions (placeholders)
function deleteScreen(id) {
    if (confirm('Вы уверены, что хотите удалить этот экран?')) {
        showNotification('Экран удален (требуется реализация API)', 'info');
    }
}

function deleteContent(id) {
    if (confirm('Вы уверены, что хотите удалить этот контент?')) {
        showNotification('Контент удален (требуется реализация API)', 'info');
    }
}

function deletePlaylist(id) {
    if (confirm('Вы уверены, что хотите удалить этот плейлист?')) {
        showNotification('Плейлист удален (требуется реализация API)', 'info');
    }
}

function deleteSchedule(id) {
    if (confirm('Вы уверены, что хотите удалить это расписание?')) {
        showNotification('Расписание удалено (требуется реализация API)', 'info');
    }
}

// Edit functions (placeholders)
function editScreen(id) {
    showNotification('Функция редактирования в разработке', 'info');
}

function editContent(id) {
    showNotification('Функция редактирования в разработке', 'info');
}

function editPlaylist(id) {
    showNotification('Функция редактирования в разработке', 'info');
}

function editSchedule(id) {
    showNotification('Функция редактирования в разработке', 'info');
}

// Utility functions
function translateStatus(status) {
    const translations = {
        'online': 'Online',
        'offline': 'Offline',
        'error': 'Error'
    };
    return translations[status] || status;
}

function translateContentType(type) {
    const translations = {
        'IMAGE': 'Изображение',
        'VIDEO': 'Видео',
        'TEXT': 'Текст',
        'HTML': 'HTML'
    };
    return translations[type] || type;
}

function formatDate(dateString) {
    if (!dateString) return 'Никогда';
    const date = new Date(dateString);
    return date.toLocaleDateString('ru-RU') + ' ' + date.toLocaleTimeString('ru-RU', {hour: '2-digit', minute:'2-digit'});
}

function formatFileSize(bytes) {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
}

function formatDuration(seconds) {
    const hours = Math.floor(seconds / 3600);
    const minutes = Math.floor((seconds % 3600) / 60);
    const secs = seconds % 60;
    
    if (hours > 0) {
        return `${hours}ч ${minutes}м`;
    } else if (minutes > 0) {
        return `${minutes}м ${secs}с`;
    } else {
        return `${secs}с`;
    }
}

function getContentPreview(item) {
    switch(item.type) {
        case 'IMAGE':
            return '<img src="/images/placeholder.png" alt="Image preview" onerror="this.style.display=\'none\'; this.parentElement.innerHTML=\'🖼️\'">';
        case 'VIDEO':
            return '🎬';
        case 'TEXT':
            return '📝';
        case 'HTML':
            return '🌐';
        default:
            return '📄';
    }
}

function showNotification(message, type = 'info') {
    const container = document.getElementById('notifications');
    const notification = document.createElement('div');
    notification.className = `notification ${type}`;
    notification.textContent = message;
    
    container.appendChild(notification);
    
    setTimeout(() => {
        notification.style.animation = 'slideIn 0.3s ease reverse';
        setTimeout(() => notification.remove(), 300);
    }, 3000);
}

function showLoading(show) {
    // Could implement a loading spinner overlay here
}

// Demo data generators
function generateDemoScreens() {
    const regions = ['Москва', 'Санкт-Петербург', 'Казань', 'Екатеринбург', 'Новосибирск'];
    const statuses = ['online', 'online', 'online', 'offline', 'error'];
    
    return Array.from({length: 15}, (_, i) => ({
        id: i + 1,
        name: `Экран #${i + 1}`,
        location: `ТЦ "Плаза", этаж ${Math.floor(Math.random() * 3) + 1}`,
        region: regions[Math.floor(Math.random() * regions.length)],
        status: statuses[Math.floor(Math.random() * statuses.length)],
        lastConnection: new Date(Date.now() - Math.random() * 86400000).toISOString()
    }));
}

function generateDemoContent() {
    const types = ['IMAGE', 'IMAGE', 'VIDEO', 'TEXT', 'HTML'];
    const names = ['Промо акция', 'Новости компании', 'Видео презентация', 'Объявление', 'Интерактивный баннер'];
    
    return Array.from({length: 10}, (_, i) => ({
        id: i + 1,
        name: names[i] || `Контент #${i + 1}`,
        type: types[i % types.length],
        size: Math.random() * 10000000 + 100000,
        createdAt: new Date(Date.now() - Math.random() * 604800000).toISOString()
    }));
}

function generateDemoPlaylists() {
    return [
        { id: 1, name: 'Утренний блок', itemCount: 5, totalDuration: 150, createdAt: new Date().toISOString() },
        { id: 2, name: 'Дневная программа', itemCount: 8, totalDuration: 240, createdAt: new Date().toISOString() },
        { id: 3, name: 'Вечерний показ', itemCount: 6, totalDuration: 180, createdAt: new Date().toISOString() }
    ];
}

function generateDemoSchedules() {
    return [
        { id: 1, name: 'Основное расписание', playlistName: 'Утренний блок', screenCount: 45, startDate: new Date().toISOString(), endDate: new Date(Date.now() + 604800000).toISOString(), active: true },
        { id: 2, name: 'Выходные дни', playlistName: 'Дневная программа', screenCount: 30, startDate: new Date().toISOString(), endDate: new Date(Date.now() + 1209600000).toISOString(), active: true }
    ];
}
